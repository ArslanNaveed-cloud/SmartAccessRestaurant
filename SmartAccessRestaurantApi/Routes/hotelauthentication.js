const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const saltRounds = 10;                          //We are setting salt rounds, higher is safer.
const myPlaintextPassword = 's0/\/\P4$$w0rD';   //Unprotected password

var {admininfo}=require("../models/AdminDataModel");
const { restaurantinfo } = require('../models/RestaurantsDataModel');
const {categoryinfo}= require('../models/CategoryDataModel');
const {fooditeminfo}= require('../models/FoodItemsDataModel');
const {orderinfo}= require('../models/OrderDataModel');
//Unprotected password
var forEach = require('async-foreach').forEach;
router.get("/loginhotel",(req,res)=>{
    res.render('hotellogin',{
        shouldshowalert:false,
        alertClass : "",
        message:""
    });
});

router.post("/loginhotel",(req,res)=>{
    var cnic = req.body.cnic;
    var code = req.body.code;
    var categorylength,orderlength,fooditemlength;
    var orderid = [];
            var hotelname =[];
            var tablenumber=[];
            var fooditems=[];
            var coverimage = [];
            var productprice = [];
            var totalbill = [];
            var productquantity = [];
            var status = [];
            
            

    var query = {
        cnic:cnic,
        code:code
    };
    restaurantinfo.findOne(query,function(err,response){
        
        if(err){
                
            console.log("Authentication Rejected");
            res.render('hotellogin',{
                shouldshowalert: true,
                message:"username/password is incorrect",
                alertClass : "alert alert-danger"
            });
        
        }
        else if(!response){
            console.log("Authentication Rejected");
            res.render('hotellogin',{
                shouldshowalert: true,
                message:"Cnic/Code is incorrect",
                alertClass : "alert alert-danger"
            });
        }
        else{
            console.log(response);
             req.session.successhotel = true;
            req.session.hotelname=response.name;
            var hotelname = req.session.hotelname;
    
            var query = {
                hotelname:hotelname
            }
            categoryinfo.find(query ,function(err,response2){
                if(err){
                    console.log(err);
                }else if(!response2){
                    categorylength = 0;
                    movetonext1();
                }else{
                   categorylength= response2.length;
                   movetonext1();

                }
            });


        }
        function movetonext1(){
            fooditeminfo.find(query,function(err,response4){
                if(err){
                    console.log(err);
                }else if(!response4){
                    fooditemlength = 0;
                    movetonext2();
                }else{
                    fooditemlength = response4.length;
                    movetonext2();
                  
                }
            });
        }

        function movetonext2(){
            orderinfo.find(query ,function(err,response5){

                if(err){
                    console.log(err);
                }else if(!response5){
                    isneworder = new Boolean(false);
                    orderlength = 0;
                    function allDone(notAborted, arr) {
                        console.log(orderid);
                        res.render("hotelindex",{
                          isneworder:isneworder,
                          hotelname:req.session.hotelname,
                          fooditemlength : fooditemlength,
                          categorylength : categorylength,
                          orderlength : orderlength,
                          orderid:orderid,
                          fooditems:fooditems,
                          tablenumber:tablenumber,
                          coverimage:coverimage,
                          productprice:productprice,
                          productquantity:productquantity,
                          totalbill:totalbill,
                          status:status
                        });
                      }
                      
                      
                      
                      forEach(response5,function(item, index) 
                      {
                         
                            var done  = this.async();
                            orderid.push(item._id);
                            fooditems.push(item.productname);
                            tablenumber.push(item.tablenumber);
                            coverimage.push(item.coverimage);
                            productprice.push(item.productprice);
                            productquantity.push(item.productquantity);
                            totalbill.push(item.totalbill);
                            status.push(item.status);
                      done();
              },allDone());
                }else {
                     isneworder = new Boolean(true);
                    orderlength =response5.length;
                   
                      
                     
                    for(i=0;i<response5.length;i++){
                       
                        orderid.push(response5[i]._id);
                        fooditems.push(response5[i].productname);
                        tablenumber.push(response5[i].tablenumber);
                        coverimage.push(response5[i].coverimage);
                        productprice.push(response5[i].productprice);
                        productquantity.push(response5[i].productquantity);
                        totalbill.push(response5[i].totalbill);
                        status.push(response5[i].status);
                    }

                    setTimeout(() => {
                        res.render("hotelindex",{
                            isneworder:isneworder,
                            hotelname:req.session.hotelname,
                            fooditemlength : fooditemlength,
                            categorylength : categorylength,
                            orderlength : orderlength,
                            orderid:orderid,
                            fooditems:fooditems,
                            tablenumber:tablenumber,
                            coverimage:coverimage,
                            productprice:productprice,
                            productquantity:productquantity,
                            totalbill:totalbill,
                            status:status
                          });
                    }, 2000);
             
                }
            });
        }
     
});

});

router.get("/logout",(req,res)=>{
    req.session.success = false;
    res.redirect("/authentication/login");
});
router.get("/loginhotel",(req,res)=>{
    res.render('hotellogin',{
        shouldshowalert:false,
        alertClass : "",
        message:""
    });
});

module.exports = router;