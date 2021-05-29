const express = require("express");
const { fooditeminfo } = require("../models/FoodItemsDataModel");
const { orderinfo } = require("../models/OrderDataModel");
const nodemailer = require("nodemailer");
const { UserInfo } = require("../models/UserDataModel");
const router = express.Router();
const bcrypt = require("bcrypt");
const saltRounds = 10;                          //We are setting salt rounds, higher is safer.
const myPlaintextPassword = 's0/\/\P4$$w0rD'; 

var PDFDocument = require('pdfkit');
var doc = new PDFDocument
const randomInt = require('random-int');
var moment = require('moment');
//var {usermodel} = require('./models/UserDataModel');
var forEach = require('async-foreach').forEach;

const fs = require('fs');


router.post("/login",(req,res)=>{
    console.log("Login API clicked");
    var email = req.body.email;
    var password = req.body.password;

    var query = {
        email:email
    }
    UserInfo.findOne(query,(err,response)=>{
        if(err){
                res.json({
                    status:500
                });
        }else if(!response){
            res.json({
                status:404
            });
        }else {
            var hash = response.password;
            bcrypt.compare(password, hash, function(error, response1) {
                if(response1){
                    res.json({
                        status:200,
                        username:response.username,
                        email:email,
                        phone:response.phone,
                        password:password,
                    });
                }else{
                    res.json({
                        status:404,
                        
                    });
                }
    });
            
        }
    });
});

router.post("/registeruser",(req,res)=>{
    var name = req.body.name;
    var email = req.body.email;
    var password = req.body.password;
    var phone = req.body.phone;
    
    bcrypt.hash(password, saltRounds, (err, hash) => {
        var hashpassword = hash;    
        var UserData = new UserInfo({
                username:name,
                email:email,
                password:hashpassword,
                phone:phone
            });
            var query={
                name:name,
                email:email
            }
            UserInfo.find(query,(err,response)=>{
                if(err){
                    res.json({
                        status:500
                    });
                }else if(response.length>=1){
                        res.json({
                            status:409
                        });
                }else{
                    UserData.save((err,result)=>{
                        if(err){
                            res.json(
                                {
                                    status:500
                                }
                            );
                        }else{
                            console.log("Register User Successfully");
                            res.json({
                                status:200,
                                username:name,
                                email:email,
                                phone:phone,
                                password:password
                            });
                        }
                    });
                }
            });
          
    });
});
router.post("/getfoodproducts",(req,res)=>{
        var  hotelname = req.body.hotelname;
        var mycategory = req.body.category;
        console.log(hotelname+""+mycategory);
        var id=[];

    var hotel = [];
    var title = [];
    var category = [];
    var description = [];
    var coverimage = [];
    var price = [];
    var query = {
        category:mycategory,
        hotelname:hotelname
    }
    fooditeminfo.find(query,(err,response)=>{
        console.log(response);
        if(err){
           res.json({
                status:500
           });
        }else if(!response){
            res.json({
                status:404
           });
        }else if(response === null){
            res.json({
                status:404
           });
        }else if(response.length<=0){
            res.json({
                status:404
           });
        }
       
        else{
            console.log(response);
            function allDone(notAborted, arr) {
                res.json({
                    status:200,
                    title:title,
                    category:category,
                    description:description,
                    coverimage:coverimage,
                    price:price,
                    hotelname:hotel,
                    id:id,
                   
                }); 
            }

            forEach(response,function(item, index) 
            {
                var done = this.async();
                id.push(item._id);
                title.push(item.title);
                hotel.push(item.hotelname);
                category.push(item.category);
                description.push(item.description);
                coverimage.push(item.coverimage);
                price.push(item.price);
                done();
            },allDone);
        }
    });
   
});

router.post("/placeorder",(req,res)=>{
    var currenttime = moment().format("h:mma");
    var now = moment().format("DD-MM-YYYY");

    var date = now+"("+currenttime+")";
    var hotelname = req.body.hotelname;
    var tablenumber = req.body.tablenumber;
    var data = req.body.data;
    var username = req.body.username;
   
    console.log("User Name =="+username);
    
    var coverimage=[];
    var productname=[];
    var productprice=[];
    var productquantity=[];
     var totalbill;
     var count = 0;
     var checkname = "";
    data = JSON.parse(data);
   console.log(data);
    function allDone(notAborted, arr) {

        // coverimage = JSON.stringify(coverimage);
        // productprice = JSON.stringify(productprice);
        // productquantity = JSON.stringify(productquantity);
        // // productname = JSON.stringify(productname);
        
        var orderdata = new orderinfo(
            {
                hotelname:hotelname,
                username:username,
                tablenumber:tablenumber,
                productname:productname,
                coverimage:coverimage,
                productprice:productprice,
                totalbill:totalbill,
                productquantity:productquantity,
                date:date,
                status:"Preparing"
            }

           
        );
        orderdata.save((err,result)=>{
            if(err){
                console.log(err);
                res.json({
                    status:500
                });
            }else{
                res.json({
                    status:200
                });
            }
        });
    }


    forEach(data,function(item, index) 
            {
              
                 var datarow = JSON.parse(item);
                 if(count ===0){
                    totalbill = datarow.Bill;
                }
                 var done  = this.async();
                 coverimage.push(datarow.coverimage);
                 productprice.push(datarow.price);
                 productquantity.push(datarow.Quantity);
                
                 console.log(totalbill);
                 if(checkname.includes(datarow.name)){

                 }else{
                    productname.push(datarow.name);
                    checkname = checkname+(datarow.name);
                  
                }
                 
               
                count++;
                done();
            },allDone);
});

router.post("/getorders",(req,res)=>{
    var username = req.body.username;
    var hotelname =[];
    var tablenumber=[];
    var fooditems=[];
    var coverimage = [];
    var productprice = [];
    var totalbill = [];
    var productquantity = [];
    var status = [];
    var date = [];
    var query = {
        username:username,
    }
    orderinfo.find(query,(err,response)=>{
        if(err){
            res.json({
                status:500
            });
        }else if(response.length>0){
           
            setTimeout(() => {
                res.json({
                    status:200,
                    hotelname:hotelname,
                    fooditems:fooditems,
                    tablenumber:tablenumber,
                    productquantity:productquantity,
                    totalbill:totalbill,
                    orderstatus:status,
                    date:date,
                });
            }, 2000);

          for(i=0;i<response.length;i++){
            hotelname.push(response[i].hotelname);
            fooditems.push(response[i].productname);
            tablenumber.push(response[i].tablenumber);
            productquantity.push(response[i].productquantity);
            totalbill.push(response[i].totalbill);
            status.push(response[i].status);
            date.push(response[i].date);
               }
             
        }
        else{
            res.json({
                status:404
            });
        }
    });

});

router.post("/forgotpassword",(req,res)=>{
    var email = req.body.email;
    const newpassword= "sar"+"_"+randomInt(10, 50000);
    
    bcrypt.hash(newpassword, saltRounds, (err, hash) => {
        UserInfo.findOne(query,(err,response)=>{
            if(err){
                res.json({
                    status:500
                });
            }else if(response.length>=1){
                UserInfo.updateOne(
                    {
                        email:email
                },{
                    $set:{
                       password:hash
                    }
                }
                ,(err,response)=>{
                    if(err){
                        response.json({
                            status:500
                        });
                    }else{
                        let transporter = nodemailer.createTransport({
                            service: 'gmail',
                            host: "smtp.ethereal.email",
                            port: 587,
                            secure: false,// true for 465, false for other ports
                            auth: {
                                user: 'mystudymanger@gmail.com',
                                pass: 'knightkingalpha'
                              },
                          });
                      
                          
                          var mailOptions = {
                            from: "Smart Access Restaurant",
                            to: req.body.email,
                            subject: 'Your Credentials',
                            text: "New Password :"+newpassword
                          };
                          
                          transporter.sendMail(mailOptions, function(error, info){
                            if (error) {
                                console.log(error);
                                
                                res.send({
                                    status:500
                                });
                            } else {
                              console.log('Email sent: ' + info.response);
                              res.json({
                                  status:200
                              });
                            }
                          });
                    }
                });
            }else{
                res.json({
                    status:500
                });
            }
        });
        
       
    });
          
    
});

router.post("/changename",(req,res)=>{
    var email = req.body.email;
    var newname = req.body.name;

    UserInfo.updateOne(
        {
            email:email
    },{
        $set:{
            username:newname,
            
        }
    }
    ,(err,response)=>{
        if(err){
            res.json({
                status:500
            });
        }else{
            res.json({
                status:200,
                username:newname
            });
        }
    });
});

router.post("/changeemail",(req,res)=>{
    var email = req.body.email;
    var newemail = req.body.newemail;

    UserInfo.updateOne(
        {
            email:email
    },{
        $set:{
            email:newemail,
            
        }
    }
    ,(err,response)=>{
        if(err){
            res.json({
                status:500
            });
        }else{
            res.json({
                status:200,
                email:newemail
            });
        }
    });
});
router.post("/changephone",(req,res)=>{
    var email = req.body.email;
    var newphone = req.body.newphone;

    UserInfo.updateOne(
        {
            email:email
    },{
        $set:{
            phone:newphone,
            
        }
    }
    ,(err,response)=>{
        if(err){
            res.json({
                status:500
            });
        }else{
            res.json({
                status:200,
                phone:newphone
            });
        }
    });
});

router.post("/changepassword",(req,res)=>{
    var email =req.body.email;
    var password = req.body.newpassword;
    bcrypt.hash(password, saltRounds, (err, hash) => {
        if(err){
            res.json({
                status:500
            });
        }else{
            UserInfo.updateOne(
                {
                    email:email
            },{
                $set:{
                    password:hash,
                    
                }
            }
            ,(err,response)=>{
                if(err){
                    res.json({
                        status:500
                    });
                }else{
                    res.json({
                        status:200,
                        password:password
                    });
                }
            });
        }
    });
    
});

router.post("/generatebill",(req,res)=>{
    var username ="Arsalan";
   
    var totalbill =Number(0);
    var tablenumber ;
    var query={
        username:username,
        status:"Done"
    }
    orderinfo.find(query,(error,response)=>{
        if(error){
            console.log(error);
        }else{
            var filepath = "./bills/bill"+username+".pdf";
            doc.pipe(fs.createWriteStream(filepath));
            doc.fillColor('black')
            .text('Bill ');
            doc.moveDown();
           
            function allDone(notAborted, arr) {
                doc.fillColor('black')
                .fontSize(10)
                .text("   Total Bill = "+totalbill+"\n"+"   Table Number = "+tablenumber);
                doc.end();
                res.json({
                    status:200
                });
            }


            forEach(response,function(item, index) 
          { 
                var done1 = this.async();

                var foodnames = item.productname;
                var quantity = item.productquantity;
                var price = item.productprice;
                totalbill = totalbill+Number(item.totalbill);
                tablenumber = item.tablenumber;
                var id = item._id;
                orderinfo.updateOne(
                    {
                        _id:id
                },{
                    $set:{
                        status:"Paid",
                        
                    }
                }
                ,(err,response)=>{
                    function allDone(notAborted, arr) {
                        done1();
                    }
        
                    forEach(foodnames,function(item, index) 
                    {
                                   
                        var done = this.async();      
                        doc.fillColor('black')
                        .fontSize(10)
                        .text((index+1)+")  "+quantity[index]+"--"+foodnames[index]+" == "+price[index]+"\n");
                        done();
                       
                        
                    },allDone);
                });
               
                
               

          },allDone);
                
        }
    });
});


module.exports =router;