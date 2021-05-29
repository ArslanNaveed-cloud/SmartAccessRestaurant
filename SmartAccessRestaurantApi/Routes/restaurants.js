const express = require('express');
const router = express.Router();

const session = require('express-session');
const bcrypt = require('bcrypt');
const randomInt = require('random-int');

const saltRounds = 10;                          //We are setting salt rounds, higher is safer.
const myPlaintextPassword = 's0/\/\P4$$w0rD';   //Unprotected password
var forEach = require('async-foreach').forEach;
var {restaurantinfo} = require("../models/RestaurantsDataModel");
router.get("/viewrestaurant",(req,res)=>{
    var name = [];
    var address = [];
    var phone = [];
    var city = [];
    var ownername = [];
    var cnic = [];
    var code = [];
    var objectid = [];
    var admincat =req.session.usercat;
    restaurantinfo.find(function(err,response){
        if(err){
            res.render("viewrestaurants",{
                shouldShowAlert:false,
                alertClass:"",
                message:"",
                isresponse:false,
                mycategory:admincat
            });
        }else{
            function allDone(notAborted, arr) {
                res.render("viewrestaurants",{
                    shouldShowAlert:false,
                    alertClass:"",
                    message:"",
                    isresponse:true,
                    mycategory:admincat,
                    name:name,
                    address:address,
                    phone:phone,
                    city:city,
                    managername:ownername,
                    cnic:cnic,
                    code:code,
                    objectid:objectid

                });
            };
            forEach(response, function(item, index) {
                var done = this.async();
                name.push(response[index].name);
                address.push(response[index].address);
                phone.push(response[index].phone);
                city.push(response[index].city);
                ownername.push(response[index].managername);
                cnic.push(response[index].cnic);
                code.push(response[index].code);
                objectid.push(response[index]._id);
                done();
                
                
            },allDone);
        }
 });
});

router.get("/addnew",(req,res)=>{
    res.render("addnewrestaurant",{
        name:"",
        address:"",
        phone:"",
        cnic:"",
        ownername:"",
        shouldshowalert: false,
        message:"",
        alertClass : ""
    });
});
router.post("/addnew",(req,res)=>{
    var name = req.body.name;
    var address =req.body.address;
    var city = req.body.city;
    var phone = req.body.phone;
    var ownername = req.body.ownername;
    var cnic = req.body.cnic;
    const code= "sar"+"_"+randomInt(10, 50000);
    var restaurnatdata = new restaurantinfo({
        name:name,
        address:address,
        city:city,
        managername:ownername,
        cnic:cnic,
        phone:phone,
        code:code
    });
    restaurnatdata.save((err,result)=>{
        if(err){
            res.render("addnewrestaurant",{
                name:name,
                address:address,
                phone:phone,
                cnic:cnic,
                ownername:managername,
                shouldshowalert: true,
                message:"Restaurant Not Added",
                alertClass : "alert alert-danger"
            });
        }else{
            res.render("addnewrestaurant",{
                name:"",
                address:"",
                phone:"",
                cnic:"",
                ownername:"",
                shouldshowalert: true,
                message:"Restaurant Added Successfully",
                alertClass : "alert alert-success"
            });
        }
    });
});

router.get("/editrestaurant?",(req,res)=>{
    var myid = req.query.id;
    var name;
    var address;
    var city;
    var phone ;
    var ownername;
    var objectid;
    var cnic;
    query = {
        _id:myid
    }
    restaurantinfo.findOne(query,function(err,response){
        objectid = response._id;
        name = response.name;
        address = response.address;
        city = response.city;
        phone = response.phone;
        ownername = response.managername;
        cnic = response.cnic;


      if(err){
       console.log(err);
      }else{
        res.render("editrestaurant",{
            objectid:objectid,
            name:name,
            address:address,
            phone:phone,
            cnic:cnic,
            ownername:ownername,
            shouldshowalert: false,
            message:"",
            alertClass : ""
        });
      }
    });
});

router.post("/editrestaurant?",(req,res)=>{
    var myid = req.body.id;
    var name = req.body.name;
    var address =req.body.address;
    var city = req.body.city;
    var phone = req.body.phone;
    var ownername = req.body.ownername;
    var cnic = req.body.cnic;
    restaurantinfo.updateOne(
        {
            _id:myid
    },{
        $set:{
            name:name,
            address:address,
            city:city,
            managername:ownername,
            cnic:cnic,
            phone:phone,
        }
    }
    ,(err,response)=>{
        if(err){
            res.render("editrestaurant",{
                name:name,
                address:address,
                phone:phone,
                cnic:cnic,
                ownername:managername,
                shouldshowalert: true,
                message:"Restaurant Not Updated",
                alertClass : "alert alert-danger"
            });
        }else{
            res.render("addnewrestaurant",{
                name:"",
                address:"",
                phone:"",
                cnic:"",
                ownername:"",
                shouldshowalert: true,
                message:"Restaurant Updated Successfully",
                alertClass : "alert alert-success"
            });
        }
    });
});



router.post("/delrestaurant",(req,res)=>{
    var myid = req.body.labor;
    var isdeleted = new Boolean(false);
    var name = [];
    var address = [];
    var phone = [];
    var city = [];
    var ownername = [];
    var cnic = [];
    var code = [];
    var objectid = [];
    var query = {
        _id:myid
    };
    restaurantinfo.deleteOne(query,function(err,response){
        if(err){
            isdeleted = false
            findrestaurants();
        }else{
            isdeleted = true;
            findrestaurants();
        }
        function findrestaurants(){
            var admincat =req.session.usercat;
    
            restaurantinfo.find(function(err,response){
                if(err){
                    res.render("viewrestaurants",{
                        shouldShowAlert:true,
                        alertClass:"alert alert-danger",
                        message:"Please Try Again",
                        isresponse:false,
                        mycategory:admincat
                    });
                }else{
                    function allDone(notAborted, arr) {
                        res.render("viewrestaurants",{
                            shouldShowAlert:true,
                            alertClass:"alert alert-sucess",
                            message:"Restaurant Deleted Successfully",
                            isresponse:true,
                            mycategory:admincat,
                            name:name,
                            address:address,
                            phone:phone,
                            city:city,
                            managername:ownername,
                            cnic:cnic,
                            code:code,
                            objectid:objectid
        
                        });
                    };
                    forEach(response, function(item, index) {
                        var done = this.async();
                        name.push(response[index].name);
                        address.push(response[index].address);
                        phone.push(response[index].phone);
                        city.push(response[index].city);
                        ownername.push(response[index].managername);
                        cnic.push(response[index].cnic);
                        code.push(response[index].code);
                        objectid.push(response[index]._id);
                        done();
                        
                        
                    },allDone);
                }
         });
        }

    });
});


module.exports = router;