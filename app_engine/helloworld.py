from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

MAP_TEMPLATE = '''
<html> 
<head> 
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script> 
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script> 
</head> 
<body> 
 
<div id="map" style="height:100%%; width:100%%;"></div> 
 
<script type="text/javascript" charset="utf-8"> 

var map; 
var centreset = false; 

function makemap()
{
    var mapOptions =  { "zoom": 11, "center": new google.maps.LatLng(55.500515, -4.128317), 
                    "mapTypeId": google.maps.MapTypeId.SATELLITE } ;
    map = new google.maps.Map(document.getElementById("map"), mapOptions);
}
 
function recorddata(lat, lng, letter, col, info)
{
    icon = 'http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld='+letter+'|'+col+'|000'
    pos = new google.maps.LatLng(lat, lng); 
 
    marker = new google.maps.Marker(
        {position: pos,
         map: map,
         title: info,
         icon: icon});
 
    infowindow = new google.maps.InfoWindow({ content: info });
    
    (function(j)
    {
        google.maps.event.addListener(j.marker, "click", 
            function()
            {
                j.infowindow.open(map, j.marker);
            });
    
     })({'marker': marker, 'infowindow': infowindow});
    
    if (!centreset)
    {
        map.setCenter(pos);
        centreset = true
    }
}
 
var jdata = %s;
 
function loaddata() 
{
 
    for (i = 0; i < jdata.length; i++)
    {
         recorddata(jdata[i][0], jdata[i][1], jdata[i][2], jdata[i][3],
                    jdata[i][4]);
    }
}
 
(function() { makemap(); loaddata(); })();
 
</script> 
 
</body> 
</html> 
 '''

BMI_UNDERWEIGHT = 0
BMI_NORMAL = 1
BMI_OVERWEIGHT = 2
BMI_OBESE = 3 

class Person(db.Model):
    id = db.IntegerProperty()
    name = db.StringProperty()
    age = db.IntegerProperty()
    weight = db.FloatProperty()
    image_path = db.StringProperty()
    capture_date = db.IntegerProperty()
    capturer_name = db.StringProperty()
    latitude = db.FloatProperty()
    longitude = db.FloatProperty()
    ref_obj_id = db.IntegerProperty()
    ref_obj_start_x = db.FloatProperty()
    ref_obj_start_y = db.FloatProperty()
    ref_obj_end_x = db.FloatProperty()
    ref_obj_end_y = db.FloatProperty()
    person_start_x = db.FloatProperty()
    person_start_y = db.FloatProperty()
    person_end_x = db.FloatProperty()
    person_end_y = db.FloatProperty()
    
    colour_map = {BMI_UNDERWEIGHT: 'FF0000',
                  BMI_NORMAL: '99FF33',
                  BMI_OVERWEIGHT: 'FFFF00',
                  BMI_OBESE: 'FF0099'}
    
    letter_map = {BMI_UNDERWEIGHT: 'U', 
                  BMI_NORMAL: 'N', 
                  BMI_OVERWEIGHT: 'o',
                  BMI_OBESE: 'O'}
    
    @classmethod
    def get_person(cls, id):
        for p in cls.all().filter('id =', id):
            return p
    
    def bmi(self):
        height_metres = self.height() / 100.0
        
        return self.weight / height_metres**2;

    def bmi_cat(self):
        b = self.bmi()
        if b >= 30.0:
            return BMI_OBESE
        elif b >= 25.0:
            return BMI_OVERWEIGHT
        elif b >= 18.5:
            return BMI_NORMAL
        else:
            return BMI_UNDERWEIGHT
                
    def height(self):
        def distance(x1, y1, x2, y2):
            x_diff = x2 - x1
            y_diff = y2 - y1
            return (x_diff**2 + y_diff**2)**0.5
        
        r = self.refobj()
        
        ref_len_px = distance(self.ref_obj_start_x, self.ref_obj_start_y,
                              self.ref_obj_end_x, self.ref_obj_end_y)
    
        person_len_px = distance(self.person_start_x, self.person_start_y,
                                 self.person_end_x, self.person_end_y)
    
        ratio_cm_px = r.length / ref_len_px
    
        return person_len_px * ratio_cm_px

    def refobj(self):
        return RefObj.get_refobj(self.ref_obj_id)
    
    def google_map_pin(self):
        bmi_cat = self.bmi_cat()
        info = 'Name: %s | Height: %.1f cm | Weight: %.1f kg | BMI: %.1f' % (
            self.name, self.height(), self.weight, self.bmi())
        # lat, long, letter, col, text
        return '[%f, %f, "%s", "%s", "%s"]' % (self.latitude, self.longitude,
                                               self.letter_map[bmi_cat],
                                               self.colour_map[bmi_cat],
                                               info)


class RefObj(db.Model):
    id = db.IntegerProperty()
    name = db.StringProperty()
    length = db.FloatProperty()
    image_path = db.StringProperty()
    
    @classmethod
    def get_refobj(cls, id):
        for r in cls.all().filter('id =', id):
            return r

def _put_obj(cls, request):
    obj = cls()
    for name in obj.fields():
        value = request.get(name)
        if value:
            data_type = getattr(cls, name).data_type            
            if data_type is float:
                value = float(value)
            elif data_type is int:
                value = int(value)
            setattr(obj, name, value)
    obj.put()
        

class RefObjPage(webapp.RequestHandler):
    def post(self):
        _put_obj(RefObj, self.request)
        
        
class PersonPage(webapp.RequestHandler):
    def post(self):
        _put_obj(Person, self.request)
    
    def get(self):
        self.response.out.write(MAP_TEMPLATE % self.google_map_pins())
    
    def google_map_pins(self):
        return '[%s]' % ','.join(p.google_map_pin() for p in Person.all())
            

class ResetPage(webapp.RequestHandler):
    def get(self):
        for p in Person.all():
            p.delete()
        for r in RefObj.all():
            r.delete()
        r = RefObj()
        r.id = 1 # SQLite3 auto inc starts at 1, not 0.
        r.name = "Ruler"
        r.length = 31.0
        r.image_path = None
        r.put()            
            
application = webapp.WSGIApplication([('/refobj', RefObjPage),
                                      ('/person', PersonPage),
                                      ('/', PersonPage),
                                      ('/reset', ResetPage)], debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
