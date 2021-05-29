var mongoose = require('mongoose');
    mongoose.Promise = global.Promise;
    var db;
    const conn = mongoose.connect('mongodb://localhost:27017/SmartAccessRestaurantDb',{ useNewUrlParser: true,useUnifiedTopology: true},(err,db)=>{
        db = db;

    });
    console.log('Database Connection Created');
module.exports={
    mongoose,
    db,
};

// pdffileinfo.deleteOne(query,function(err,response){
// });


// FIND ALL
// pdffileinfo.find(query,function(err,response){
// });

// FIND ONE
// pdffileinfo.find(query,function(err,response){
// });

// SAVE ALL
//collectionData.save((err,result)=>{
//     variables
// }
// pdffiledata.save(,function(err,response){
// });

// shouldshowalert: true,
// message:"username/password is incorrect",
// alertClass : "alert alert-danger"