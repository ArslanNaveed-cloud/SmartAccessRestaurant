const express = require('express');
const router = express.Router();

const session = require('express-session');
const bcrypt = require('bcrypt');
const saltRounds = 10;                          //We are setting salt rounds, higher is safer.
const myPlaintextPassword = 's0/\/\P4$$w0rD';   //Unprotected password
var forEach = require('async-foreach').forEach;

var {admininfo}=require("../models/AdminDataModel");
router.get('/viewadmins',(req,res)=>{
    var name=[]
    var email=[]
    var category= [];
    var phone= [];
    var id = [];
    var objectid = [];
    var admincat =req.session.usercat;
    
        console.log(admincat);
        admininfo.find(function(err,response){
        
            if(err){
                    
                console.log("Error Occured");
                res.render("viewadmins",{
                    isadminresponse:false,
                    mycategory:admincat
                });
               
            
            }
            else if(!response){
                console.log("Admins Not Found");
                res.render("viewadmins",{
                    isadminresponse:false,
                    mycategory:admincat,
                    shouldShowAlert:false,
                    message:"",
                    alertClass:""
                });
               
            }
            else{
                console.log(response);
                console.log(response.username);
                
                console.log("Authentication Successfull");
                var name = [];
                var email = [];
                var admintype = [];
                var phone = [];
                function allDone(notAborted, arr) {
                    res.render("viewadmins",{
                        isadminresponse:true,
                        mycategory:admincat,
                        shouldShowAlert:false,
                        message:"",
                        alertClass:"",
                        name:name,
                        email:email,
                        phone:phone,
                        category:admintype,
                        objectid:objectid
                    });
                  }
                forEach(response, function(item, index) {
                    var done = this.async();
                    name.push(response[index].name);
                    email.push(response[index].email);
                    admintype.push(response[index].category);
                    phone.push(response[index].phone);
                    objectid.push(response[index]._id);
                
                    done();
                },allDone);
              
               
            }
            
    });
    
   
});

router.get('/addnewadmin',(req,res)=>{
    res.render("addnewadmin",{
        shouldShowAlert:false,
        name:"",
        email:"",
         phone:""
    });
});

router.post('/addnewadmin',(req,res)=>{
    var name =req.body.name;
    var email =req.body.email;
    var phone =req.body.phone;
    var category =req.body.category;
    var password = req.body.password;
    var repassword = req.body.repassword;
    if(password === repassword){
        var query = {
            email : email
        };
        admininfo.findOne(query,function(err,response){
            
            if(err){
                    
                console.log("Authentication Rejected");
                //adminnot exist
            
            }
            else if(!response){
                console.log("Authentication Rejected");
                bcrypt.hash(password, saltRounds, (err, hash) => {
                    var admindata  =new admininfo(
                        {
                        name:name,
                        email:email,
                        phone:phone,
                        category:category,
                        password:hash
                    }
                    );
            
                    admindata.save((err,result)=>{
                            if(err){
                                res.send(err);
                            }else{
                                res.render("addnewadmin",{
                                    shouldShowAlert:true,
                                    name:"",
                                    email:"",
                                    address:"",
                                    phone:"",
                                    message:"Admin Added Successfully",
                                    alertClass : "alert alert-success",
                            
                                });
                            }
                    });
                });
            }
            else{
                res.render("addnewadmin",{
                    shouldShowAlert:true,
                    name:name,
                    email:email,
                    phone:phone,
                    message:"Admin with same Email Already Exist",
                    alertClass : "alert alert-danger",
            
                });
    
            }
            
    });
    }else{
        res.render('addnewadmin',{
            shouldShowAlert:true,
            message:"Password Donot match",
            name:name,
                email:email,
                address:address,
                phone:phone,
            alertClass : "alert alert-danger",
        });
    }
    console.log("name ="+name);
    
  
    
    
    
});

router.get("/editadmin",(req,res)=>{
    var myid = req.query.id;
    var name,email,phone;
     var objectid;
    var admincat =req.session.usercat;
    var query = {
        _id:myid
    };
        console.log(admincat);
        admininfo.findOne(query,function(err,response){
        
            if(err){
                    
                console.log("Error Occured");
                res.render("editadmin",{
                    isadminresponse:false,
                    mycategory:admincat
                });
               
            
            }
            else if(!response){
                console.log("Admins Not Found");
                res.render("editadmin",{
                    isadminresponse:false,
                    mycategory:admincat
                });
               
            }
            else{
                console.log(response);
                
                console.log("Successfull");
               var name = response.name;
               var email = response.email;
               var phone = response.phone;
               var objectid   = response._id;
                res.render("editadmin",{
                    isadminresponse:true,
                    shouldShowAlert:false,
                    alertClass:"",
                    message:"",
                    mycategory:admincat,
                    name:name,
                    email:email,
                    phone:phone,
                    objectid:objectid
                });
              
               
            }
            
    });
    
   
});

router.post("/editadmin",(req,res)=>{
    var name =req.body.name;
    var email =req.body.email;
    var phone =req.body.phone;
    var category =req.body.category;
    var id = req.body.id;
    admininfo.updateOne(
        {
            _id:id
    },{
        $set:{
            name:name,
       email:email,
       phone:phone,
       category:category
        }
    }
    ,(err,response)=>{
        if(err){
            res.render("editadmin",{
                isadminresponse:true,
                shouldShowAlert:false,
                alertClass:"alert alert-danger",
                message:"Admin Cannot be Updated",
                name:name,
                email:email,
                phone:phone,
                objectid:id
            });
        }else{
            res.render("editadmin",{
                isadminresponse:true,
                shouldShowAlert:true,
                alertClass:"alert alert-success",
                message:"Admin Updated Successfully",
                
                name:"",
                email:"",
                phone:"",
                objectid:""
            });
        }
    
  });
    
});

router.post("/deladmin",(req,res)=>{
    var myid  = req.body.labor;
    var name=[]
    var email=[]
    var category= [];
    var phone= [];
    var id = [];
    var objectid = [];
    var alertClass="alert alert-danger";
 var message="Category Updated Successfully";
    var query = {
        _id:myid
    };
    var isdeleted = new Boolean(false);
    admininfo.deleteOne(query,function(err,response){
        if(err){
            isdeleted = false
            findadmins();
        }else{
            isdeleted = true;
            findadmins();
        }

    });
    var admincat =req.session.usercat;
    
        console.log(admincat);
        function findadmins(){
            
            admininfo.find(function(err,response){
                if(isdeleted){
                    alertClass = "alert alert-success";
                    message = "Admin Deleted Successfully";
                }else{
                    alertClass = "alert alert-danger";
                    message = "Please Try Again Later";
                }
                if(err){
                        
                    console.log("Error Occured");
                    res.render("viewadmins",{
                        isadminresponse:false,
                        mycategory:admincat
                    });
                   
                
                }
                else if(!response){
                    console.log("Admins Not Found");
                    res.render("viewadmins",{
                        isadminresponse:false,
                        mycategory:admincat,
                        shouldShowAlert:false,
                        message:"",
                        alertClass:""
                    });
                   
                }
                else{
                    console.log(response);
                    console.log(response.username);
                    
                    console.log("Authentication Successfull");
                    var name = [];
                    var email = [];
                    var admintype = [];
                    var phone = [];
                    function allDone(notAborted, arr) {
                        res.render("viewadmins",{
                            isadminresponse:true,
                            mycategory:admincat,
                            shouldShowAlert:true,
                            message:message,
                            alertClass:alertClass,
                            name:name,
                            email:email,
                            phone:phone,
                            category:admintype,
                            objectid:objectid
                        });
                      }
                    forEach(response, function(item, index) {
                        var done = this.async();
                        name.push(response[index].name);
                        email.push(response[index].email);
                        admintype.push(response[index].category);
                        phone.push(response[index].phone);
                        objectid.push(response[index]._id);
                    
                        done();
                    },allDone);
                  
                   
                }
                
        });
        
        }
});

module.exports = router;