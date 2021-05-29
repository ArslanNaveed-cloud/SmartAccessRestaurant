var mongoose = require('mongoose');
  
var restaurantinfo = mongoose.model('restaurantinfo',{
    name:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
     address:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
    
    city:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
    managername:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
    cnic:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
    phone:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
    code:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
    
});
module.exports = {
    restaurantinfo
}