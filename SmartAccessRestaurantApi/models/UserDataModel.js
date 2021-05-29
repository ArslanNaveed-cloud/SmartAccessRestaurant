var mongoose = require('mongoose');
  
var UserInfo = mongoose.model('UserInfo',{
      
    username:{
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
    password:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
});
module.exports = {
    UserInfo
}