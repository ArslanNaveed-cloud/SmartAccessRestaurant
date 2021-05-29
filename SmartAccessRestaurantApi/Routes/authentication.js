const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const saltRounds = 10;                          //We are setting salt rounds, higher is safer.
const myPlaintextPassword = 's0/\/\P4$$w0rD';   //Unprotected password

var {admininfo}=require("../models/AdminDataModel");
const { restaurantinfo } = require('../models/RestaurantsDataModel');
const { UserInfo } = require('../models/UserDataModel');

var forEach = require('async-foreach').forEach;
router.get('/login',(req,res)=>{
    res.render('login',{
        shouldshowalert:false,
        alertClass : "",
        message:""
    });
  
});

router.post("/login",(req,res)=>{
    var username= [];
    var useremails=[];
    var userphone= [];
    var objectid=[];
    var email = req.body.email;
    var password = req.body.password;
    //12345678
    var query = {
        email:email,
       
    };
    admininfo.findOne(query,function(err,response){
        
        if(err){
                
            console.log("Authentication Rejected");
            res.render('login',{
                shouldshowalert: true,
                message:"username/password is incorrect",
                alertClass : "alert alert-danger"
            });
        
        }
        else if(!response){
            console.log("Authentication Rejected");
            res.render('login',{
                shouldshowalert: true,
                message:"username/password is incorrect",
                alertClass : "alert alert-danger"
            });
        }
        else{
            console.log(response);
            console.log(response.category);
            req.session.success = true;
            req.session.usercat = response.category;
           
            var hashpassword = response.password;
            bcrypt.compare(password, hashpassword, function(error, response) {
                if(response){
                    
                    console.log(req.session.usercat);
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
                    
                    
                    


                   
                }else{
                    
                    res.render('login',{
                        shouldshowalert: true,
                        message:"username/password is incorrect",
                        alertClass : "alert alert-danger"
                    });
                }
            }); 

        }
        
});

});

router.get("/logout",(req,res)=>{
    req.session.success = false;
    res.redirect("/authentication/login");
});
router.post("/deluser",(req,res)=>{
    var id = req.body.labor;
    var username= [];
    var useremails=[];
    var userphone= [];
    var objectid=[];
    var query = {
        _id:id
    }
    UserInfo.deleteOne(query,(err,respponse)=>{
        if(err){
            console.log(err);
        }else{
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
                            
                            
                            
        
        
                   
        
        }
    });
});

module.exports = router;