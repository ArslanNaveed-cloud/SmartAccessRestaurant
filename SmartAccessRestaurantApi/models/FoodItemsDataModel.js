var mongoose = require('mongoose');
  
var fooditeminfo = mongoose.model('fooditeminfo',{
    title:{
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
    
    hotelname:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
    description:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
    
    price:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
    coverimage:{
        type:String,
        required:true,
        minlength:1,
        trim:true
    },
});
module.exports = {
    fooditeminfo
}