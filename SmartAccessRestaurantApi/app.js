var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');
var EJS  = require('ejs');
const session = require('express-session');
var app  = express();
var {conn} = require('./db/mongoose');
const ImagesToPDF = require('images-pdf');
const fsExtra = require('fs-extra')

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.set('view engine','ejs');
app.set('views', path.join(__dirname, 'views'));

app.engine('html', EJS.renderFile);
app.use(express.static(path.join(__dirname,'public')));
app.use(session({
    key: 'user_sid',
    secret: 'KLAKLSDAmlad1233A^&*?:>DADLpkvnjvnbsbjz@#@',
    rolling: true,
    resave: false,
    saveUninitialized: false,
  }));
  var checker = (req,res,next) => {
   
    if(req.session.success){
        next();
    }else{
     
        res.redirect("/authentication/login");
    }

};
var checker2 = (req,res,next) => {
   
    if(req.session.successhotel){
        next();
    }else{
     
        res.redirect("/hotelauthentication/loginhotel");
    }

};

app.use("/index" ,checker, require("./Routes/homepage"));
app.use("/admin" ,checker, require("./Routes/admins"));
app.use("/authentication" , require("./Routes/authentication"));
app.use("/hotelauthentication" , require("./Routes/hotelauthentication"));
app.use("/restaurant",checker,require("./Routes/restaurants"));
app.use("/hotel",checker2,require("./Routes/hotel"));
app.use("/api",require("./Routes/api"));

app.listen(3000,(err)=>{
    if(err){
        return console.log(err);
    }
     console.log('Starting on port 3000');
 });
