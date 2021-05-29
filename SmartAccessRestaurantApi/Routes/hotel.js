const express = require('express');
const router = express.Router();
var QRCode = require('qrcode');
const randomInt = require('random-int');
const fs = require('fs');
const multer = require("multer");
const saltRounds = 10;                          //We are setting salt rounds, higher is safer.
const myPlaintextPassword = 's0/\/\P4$$w0rD';   //Unprotected password
var forEach = require('async-foreach').forEach;
const ImagesToPDF = require('images-pdf');
const imgToPDF = require('image-to-pdf');
var PDFDocument = require('pdfkit');
var doc = new PDFDocument
const fsExtra = require('fs-extra')
const http = require('http');
const directory = './qrcodes';
const {categoryinfo} =require("../models/CategoryDataModel");
const {fooditeminfo} =require("../models/FoodItemsDataModel");
const {orderinfo} =require("../models/OrderDataModel");

var path = require('path');
var imagearray=[];
var random  = randomInt(10, 50000);
const ptp = require("pdf-to-printer");
const storage = multer.diskStorage({
    destination: './public/assets/foodimages/',
    filename: function(req, file,cb){
        cb(null,file.fieldname+ '_'+random+file.originalname);
        imagearray.push(file.fieldname+ '_'+random+file.originalname);
        console.log("Image Added");
        
    }
});

const upload  = multer({
    storage:storage,
   
    fileFilter :function(req,file,cb){
        checkFileType(file,cb);

    }
}).single('image');

function checkFileType(file,cb){
    if (file.mimetype === 'image/jpg' || file.mimetype === 'image/jpeg' || file.mimetype === 'image/png') {
        cb(null, true);
      } else {
        cb("Error.Only Upload Image File", false);
      }
}
router.get("/",(req,res)=>{
    var hotelname = req.session.hotelname;
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

router.get("/generateqrcode",(req,res)=>{
    res.render("addqrcode",{
        shouldShowAlert:false,
        message:"",
        alertClass:"",
        hotelname:req.session.hotelname
    });

});
router.post("/generateqrcode",(req,res)=>{
    var from = req.body.from;
    var to = req.body.to;
    var counter = 0;
    var hotelname = req.session.hotelname;
    var dummyarray= [];
    var list = [];
    for(i=from;i<=to;i++){
        dummyarray.push(i);
    }
    setTimeout(() => {
        function allDone(notAborted, arr) {
            setTimeout(() => {
                // new ImagesToPDF.ImagesToPDF().convertFolderToPDF('./qrcodes', 'qrcodepdf/file.pdf');
                imgToPDF(list, "A4")
                .pipe(fs.createWriteStream('./qrcodepdf/file.pdf'));
 
                setTimeout(() => {
                            fsExtra.emptyDirSync(directory)
                        res.download("./qrcodepdf/file.pdf");
                   
                        }, 1000);
                
               
            },3000);
           
            }
        forEach(dummyarray, function(item, index) {
            var done = this.async();
           
            console.log(item);
            var json = {
                hotelname:hotelname,
                tablenumber:item
        }
        json = JSON.stringify(json);
            var imagepath = "./qrcodes/"+hotelname+""+item+".png";
            list.push(imagepath);
            QRCode.toFile("qrcodes/"+hotelname+""+item+".png",json,
            {
                width:150
            });
           
             
            done();
      
        },allDone);
       
    }, 1000);
  
});

router.get("/viewcategory",(req,res)=>{
    var name =[];
    var id=[];
    var hotelname = req.session.hotelname;
    var query = {
        hotelname:hotelname
    }
    categoryinfo.find(query ,function(err,response){
        if(err){
            console.log(err);
        }else{
            function allDone(notAborted, arr) {
                res.render("viewcategories",{
                    updated:false,
                    iscatresponse:true,
                    name:name,
                    id:id,
                    hotelname:req.session.hotelname
                });
            }
            forEach(response, function(item, index) {
                var done = this.async();
                name.push(response[index].name);
                id.push(response[index]._id);
                done();
            },allDone);

        }
    });
        
    
    
});

router.get("/addnewcategory",(req,res)=>{
    res.render("addnewcategory",{
        shouldshowalert: false,
        message:"",
        alertClass : "",
        name:"",
        hotelname:req.session.hotelname

    });
});
router.post("/addnewcategory",(req,res)=>{
    var name = req.body.name;
    var hotelname = req.session.hotelname;
    var categorydata = new categoryinfo({
        name:name,
        hotelname:hotelname
    });
    var query = {
        name:name
    }
    categoryinfo.find(query,function(err,response){
        if(err){
            console.log(err);
        }else if(response.length>=1){
            res.render("addnewcategory",{
                shouldshowalert: true,
                message:"Category Aleady Exists",
                alertClass : "alert alert-danger",
                name:name,
                hotelname:req.session.hotelname
            });
        }else{
            categorydata.save((err,result)=>{
                if(err){
                    res.render("addnewcategory",{
                        shouldshowalert: true,
                        message:"Please Try Again",
                        alertClass : "alert alert-danger",
                        name:name,
                        hotelname:req.session.hotelname
                    });
                }else{
                    res.render("addnewcategory",{
                        shouldshowalert: true,
                        message:"Category Added Successfully",
                        alertClass : "alert alert-success",
                        name:"",
                        hotelname:req.session.hotelname
                    });
                }
            });
        }    
    });
        
   
});
router.get("/editcategory?",(req,res)=>{
    var id = req.query.id;
    var myname,objectid ;
    var query = {
        _id:id
    }
    categoryinfo.findOne(query,function(err,response){
            if(err){
                console.log(err);
            }else{
                myname = response.name;
                objectid = response._id;
                res.render("editcategory",{
                shouldshowalert: false,
                message:"",
                alertClass : "",
                objectid:objectid,
                name:myname,
                hotelname:req.session.hotelname
                });
            }
    });
});

router.post("/editcategory?",(req,res)=>{
    var id = req.body.id;
    var name = req.body.name;

    categoryinfo.updateOne(
        {
            _id:id
    },{
        $set:{
            name:name,
      
        }
    }
    ,(err,response)=>{
        if(err){
            res.render("editcategory",{
                shouldshowalert: true,
                message:"Category Not Updated",
                alertClass : "alert alert-danger",
                objectid:objectid,
                name:myname,
                hotelname:req.session.hotelname
                });
        }else{
            res.render("editcategory",{
                shouldshowalert: true,
                message:"Category Updated Successfully",
                alertClass : "alert alert-success",
                objectid:"",
                name:"",
                hotelname:req.session.hotelname
                });
        }
    });
});

router.post("/delcategory",(req,res)=>{
    var myid  = req.body.labor;
    var query = {
        _id:myid
    };
    var alertClass = "";
    var message = "";
    var isdeleted = new Boolean(false);
    categoryinfo.deleteOne(query,function(err,response){
        if(err){
            isdeleted = false;
            findcategories();
        }else{
            isdeleted = true;
            findcategories();
        }

        function  findcategories(){
            var name =[];
            var id=[];
            if(isdeleted){
                alertClass = "alert alert-success";
                message = "Category Deleted Successfully";
            }else{
                alertClass = "alert alert-danger";
                message = "Please Try Again Later";
            }
            categoryinfo.find(function(err,response){
                if(err){
                    console.log(err);
                }else{
                    function allDone(notAborted, arr) {
                        res.render("viewcategories",{
                            updated:false,
                            iscatresponse:true,
                            name:name,
                            id:id,
                            hotelname:req.session.hotelname
                        });
                    }
                    forEach(response, function(item, index) {
                        var done = this.async();
                        name.push(response[index].name);
                        id.push(response[index]._id);
                        done();
                    },allDone);
        
                }
            });
                
        }
    });

});

router.get("/viewfoodproducts",(req,res)=>{
    var hotelname = req.session.hotelname;
    var id=[];
    var hotel = [];
    var title = [];
    var category = [];
    var description = [];
    var coverimage = [];
    var price = [];
    var query = {
        hotelname:hotelname
    }
    fooditeminfo.find(query,(err,response)=>{
        if(err){
            res.render("viewfooditems",{
                isfooditemresponse:false,
                shouldShowAlert:false,
                
            }); 
        }else if(!response){
            res.render("viewfooditems",{
                isfooditemresponse:false,
                shouldShowAlert:false,
                
            }); 
        }
        
        else{
            function allDone(notAborted, arr) {
                res.render("viewfooditems",{
                    isfooditemresponse:true,
                    shouldShowAlert:false,
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
router.get("/addnewfooditem",(req,res)=>{
    var query = {
        hotelname:req.session.hotelname
    }
    categoryinfo.find(query,function(err,response){
         if(err){
             console.log(err);
         }else{
            var category = [];
   
            function allDone(notAborted, arr) {
                res.render("addnewfooditem",{
                    shouldshowalert:false,
                    iscategoryresponse:true,
                    hotelname:req.session.hotelname,
                    name:"",
                    description:"",
                    price:"",
                    category:category

                });
            }
            forEach(response, function(item, index) {
                var done = this.async();
                category.push(item.name);
                done();
            },allDone);
         }
    });
    
});
router.post("/addnewfooditem",(req,res)=>{
    var title = req.body.title;
    var mycategory = req.body.category;
    var description = req.body.description;
    var price = req.body.price;
     var hotelname = req.session.hotelname;
     var image;
    var query1 = {
        hotelname : req.session.hotelname
    }
    var alreadypresent = new Boolean(false);
  
    
    upload(req,res,(err)=>{
        title = req.body.title;
        mycategory = req.body.category;
        description = req.body.description;
        price = req.body.price;
        hotelname = req.session.hotelname;
        image;
        query1 = {
            hotelname : req.session.hotelname
        }
        var alreadypresent = new Boolean(false);
      

         image = imagearray[0];
   
        if(err){
            console.log(err);
        }else{
            var query = {
                title:title,
                hotelname:hotelname
            }
            fooditeminfo.find(query,function(err,response){
                if(err){
                    console.log(err);
                }    else{
                    if(response.length>=1){
                        alreadypresent = new Boolean(true);
                        viewcategory(alreadypresent);
                    }else{
                        alreadypresent = new Boolean(false);
                        viewcategory();
                    }
                }
            });
        }
      
    
    });
    function viewcategory(alreadypresent){
       
        categoryinfo.find(query1,function(err,response){
            if(err){
                console.log(err);
            }else{
               var category = [];
      
               function allDone(notAborted, arr) {
                   if(alreadypresent){
                    res.render("addnewfooditem",{
                        shouldshowalert:true,
                        iscategoryresponse:true,
                        hotelname:req.session.hotelname,
                        name:title,
                        description:description,
                        price:price,
                        category:category,
                        message:"Product With Same Name Already Exists",
                        alertClass :"alert alert-danger"
                    });
                   }else{
                       var fooditemdata = new fooditeminfo({
                           title:title,
                           category:mycategory,
                           hotelname:req.session.hotelname,
                           description:description,
                           price:price,
                           coverimage:image
                       });
                       fooditemdata.save((err,result)=>{
                           if(err){
                               console.log(err);
                            res.render("addnewfooditem",{
                                shouldshowalert:true,
                                iscategoryresponse:true,
                                hotelname:req.session.hotelname,
                                name:title,
                                description:description,
                                price:price,
                                category:category,
                                message:"Please Try Again Later",
                                alertClass:"alert alert-danger"
                            }); 
                           }else{
                            res.render("addnewfooditem",{
                                shouldshowalert:true,
                                iscategoryresponse:true,
                                hotelname:req.session.hotelname,
                                name:"",
                                description:"",
                                price:"",
                                category:category,
                                message:"Product Addded Successfully",
                                alertClass:"alert alert-success"
                            });
                           }
                       });
                   }

                   
               }
               forEach(response, function(item, index) {
                   var done = this.async();
                   category.push(item.name);
                   done();
               },allDone);
            }
       });
       
    }
});

router.get("/editfooditem",(req,res)=>{
    var id = req.query.id;
    var myid;
    var title,category,description,price,hotelname,coverimage;
    var query = {
        _id:id
    }
    fooditeminfo.findOne(query,(err,response)=>{
        if(err){
            console.log(err);
        }else{
            myid = response._id;
            title = response.title;       
            category = response.category;
            description = response.description;
            price = response.price;
            hotelname = response.hotelname;
            coverimage = response.coverimage;
            var query = {
                hotelname:req.session.hotelname
            }
            categoryinfo.find(query,function(err,response){
                 if(err){
                     console.log(err);
                 }else{
                    var category = [];
           
                    function allDone(notAborted, arr) {
                        res.render("editfooditem",{
                            shouldshowalert:false,
                            iscategoryresponse:true,
                            id:myid,
                            hotelname:req.session.hotelname,
                            name:title,
                            description:description,
                            price:price,
                            category:category
                            
                        });
                    }
                    forEach(response, function(item, index) {
                        var done = this.async();
                        category.push(item.name);
                        done();
                    },allDone);
                 }
            });
           

        }
    });
});
router.post("/editfooditem",(req,res)=>{
    var id = req.body.id;
    var title = req.body.title;
    var mycategory = req.body.category;
    var description = req.body.description;
    var price = req.body.price;
     var hotelname = req.session.hotelname;
     var image;
    var query1 = {
        hotelname : req.session.hotelname
    }

    upload(req,res,(err)=>{
        title = req.body.title;
        mycategory = req.body.category;
        description = req.body.description;
        price = req.body.price;
        hotelname = req.session.hotelname;
        var updated = new Boolean(false);
        image;
        query1 = {
            hotelname : req.session.hotelname
        }
           image = imagearray[0];
   
        if(err){
            console.log(err);
        }else{
           viewcategory();
          
        }
      
    
    });
    function viewcategory(){
       
        categoryinfo.find(query1,function(err,response){
            if(err){
                console.log(err);
            }else{
               var category = [];
      
               function allDone(notAborted, arr) {
                categoryinfo.updateOne(
                    {
                        _id:id
                },{
                    $set:{
                        title:title,
                        category:category,
                        description:description,
                        coverimage:image,
                        price:price,
                        hotelname:req.session.hotelname
                    }
                }
                ,(err,response)=>{
                        if(err){
                            var updated = new Boolean(false);
                            checkupdate(updated);
                        
                                }else{
                                    var updated = new Boolean(true);
                                    checkupdate(updated);
                        
                                }
                            }); 
                        
                

                   
               }
               forEach(response, function(item, index) {
                   var done = this.async();
                   category.push(item.name);
                   done();
               },allDone);
            }
       });
       
    }
    function checkupdate(updated){
        var query = {
            hotelname:req.session.hotelname
        }
      
                categoryinfo.find(query,function(err,response){
                     if(err){
                         console.log(err);
                     }else{
                        var category = [];
               
                        function allDone(notAborted, arr) {
                            if(updated){
                                res.render("editfooditem",{
                                    shouldshowalert:true,
                                    iscategoryresponse:true,
                                    message:"Food Item Updated Successfully",
                                    alertClass:"alert alert-success",
                                    id:"",
                                    hotelname:"",
                                    name:"",
                                    description:"",
                                    price:"",
                                    category:""
                                    
                                });
                            }else{
                                res.render("editfooditem",{
                                    shouldshowalert:true,
                                    iscategoryresponse:true,
                                    message:"Please Try Again Later",
                                    alertClass:"alert alert-danger",
                                    id:id,
                                    hotelname:req.session.hotelname,
                                    name:title,
                                    description:description,
                                    price:price,
                                    category:category
                                    
                                });
                            }
                           
                        }
                        forEach(response, function(item, index) {
                            var done = this.async();
                            category.push(item.name);
                            done();
                        },allDone);
                     }
                });
               
    };
});
router.post("/delfooditem",(req,res)=>{
    var myid = req.body.labor;
    var query = {
        _id:myid
    };
    var alertClass = "";
    var message = "";
    var isdeleted = new Boolean(false);
    fooditeminfo.deleteOne(query,function(err,response){
        if(err){
            var isdeleted = new Boolean(false);
            findcategories(isdeleted);
        }else{
            var isdeleted = new Boolean(true);
            findcategories(isdeleted);
        }});
        
        function findcategories(){

            var hotelname = req.session.hotelname;
            var id=[];
            var hotel = [];
            var title = [];
            var category = [];
            var description = [];
            var coverimage = [];
            var price = [];
            var query = {
                hotelname:hotelname
            }
            fooditeminfo.find(query,(err,response)=>{
                if(err){
                    res.render("viewfooditems",{
                        isfooditemresponse:false,
                        shouldShowAlert:false,
                        
                    }); 
                }else if(!response){
                    res.render("viewfooditems",{
                        isfooditemresponse:false,
                        shouldShowAlert:false,
                        
                    }); 
                }
                
                else{
                    function allDone(notAborted, arr) {
                        
                        if(isdeleted){
                            res.render("viewfooditems",{
                                isfooditemresponse:true,
                                shouldShowAlert:true,
                                message:"Food Item Deleted Successfully",
                                alertClass:"alert alert-success",
                                title:title,
                                category:category,
                                description:description,
                                coverimage:coverimage,
                                price:price,
                                hotelname:hotel,
                                id:id,
                               
            
                            }); 
                        }else{
                            res.render("viewfooditems",{
                                isfooditemresponse:true,
                                shouldShowAlert:true,
                                message:"Please Try Again Later",
                                alertClass:"alert alert-danger",
                                title:title,
                                category:category,
                                description:description,
                                coverimage:coverimage,
                                price:price,
                                hotelname:hotel,
                                id:id,
                               
            
                            }); 
                        }
                       
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
           


        }
});
router.get("/preparestatus?",(req,res)=>{
    var id = req.query.id;
    var categorylength,orderlength,fooditemlength;
    orderinfo.updateOne(
        {
            _id:id
    },{
        $set:{

            status:"Preparing"
        }},(err,response)=>{
            if(err){
                console.log(err);
            }else{
                var query = {
                    hotelname:req.session.hotelname
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
                            var orderid = [];
            var hotelname =[];
            var tablenumber=[];
            var fooditems=[];
            var coverimage = [];
            var productprice = [];
            var totalbill = [];
            var productquantity = [];
            var status = [];
            
            
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
            }
        });
});
router.get("/donestatus?",(req,res)=>{
    var id = req.query.id;
    var categorylength,orderlength,fooditemlength;
    orderinfo.updateOne(
        {
            _id:id
    },{
        $set:{

            status:"Done"
        }},(err,response)=>{
            if(err){
                console.log(err);
            }else{
                var query = {
                    hotelname:req.session.hotelname
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
                            var orderid = [];
            var hotelname =[];
            var tablenumber=[];
            var fooditems=[];
            var coverimage = [];
            var productprice = [];
            var totalbill = [];
            var productquantity = [];
            var status = [];
            
            
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
            }
        });
});
router.get("/cancelorder?",(req,res)=>{
    var id = req.query.id;
    var categorylength,orderlength,fooditemlength;
    orderinfo.updateOne(
        {
            _id:id
    },{
        $set:{

            status:"Cancel"
        }},(err,response)=>{
            if(err){
                console.log(err);
            }else{
                var query = {
                    hotelname:req.session.hotelname
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
                            var orderid = [];
            var hotelname =[];
            var tablenumber=[];
            var fooditems=[];
            var coverimage = [];
            var productprice = [];
            var totalbill = [];
            var productquantity = [];
            var status = [];
            
            
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
            }
        });
});

router.get("/printbill",(req,res)=>{
    var orderid = req.query.orderid;
    var query={
        _id:orderid
    }
    orderinfo.find(query,(error,response)=>{
        if(error){
            console.log(error);
        }else{
            doc.pipe(fs.createWriteStream('./bills/bill.pdf'));
            doc.fillColor('black')
            .text('Bill ');
            
           

            var foodnames = response[0].productname;
            var quantity = response[0].productquantity;
            var price = response[0].productprice;
            var totalbill = response[0].totalbill;
            var tablenumber = response[0].tablenumber;
            doc.moveDown();
           

                       for(var i = 0;i<foodnames.length;i++){
                               
                            
                            doc.fillColor('black')
                            .fontSize(10)
                            .text((i+1)+")  "+quantity[i]+"--"+foodnames[i]+" == "+price[i]+"\n");
                            
                        setTimeout(() => {
                            doc.fillColor('black')
                .fontSize(10)
                .text("   Total Bill = "+totalbill+"\n"+"   Table Number = "+tablenumber);
                   doc.end();
                            
                   }, 1000);
                           
                            
                        }
                            
               
        }
    });
});
module.exports = router;