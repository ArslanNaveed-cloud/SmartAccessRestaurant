const express = require('express');
const router = express.Router();
const {restaurantinfo} = require("../models/RestaurantsDataModel");
const {admininfo} = require("../models/AdminDataModel");

router.get('/',(req,res)=>{
    var id = req.body.labor;
    var username= [];
    var useremails=[];
    var userphone= [];
    var objectid=[];
   
    restaurantinfo.find(function(err,response1){
        if(err){
            console.log(err);
        }else{
            admininfo.find(function(err,response){
                if(err){
                    console.log(err);
                }else if(response.length>=1){
                    UserInfo.find((err,userresponse)=>{
                        function allDone(notAborted, arr) {
                            res.render("index",{
                                isuserresponse:true,
                                restaurantlength:response1.length,
                                adminlength:response.length,
                                userlength:userresponse.length,
                                username:username,
                                useremail:useremails,
                                userphone:userphone,
                                objectid:objectid
                               });
                        }
                        forEach(userresponse,function(item, index) 
                                {
                                    var done  = this.async();
                                        username.push(item.username);
                                        useremails.push(item.email);
                                        userphone.push(item.phone);
                                        objectid.push(item._id);
                                        done();
                                    },allDone);
                    });




                   
                }else{
                    res.render("index",{
                        isuserresponse:false,
                        restaurantlength:response1.length,
                        adminlength:response.length,
                        userlength:0,
                        

                       });
                }
                });
            
        }
        });
    
    
    
  
});

module.exports = router;