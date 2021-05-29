var mongoose = require('mongoose');
  
var admininfo = mongoose.model('admininfo',{
    name:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
     email:{
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
    category:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
    
    password:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
});
module.exports = {
    admininfo
}