var mongoose = require('mongoose');
  
var categoryinfo = mongoose.model('categoryinfo',{
    name:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
    hotelname:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
});
module.exports = {
    categoryinfo
}