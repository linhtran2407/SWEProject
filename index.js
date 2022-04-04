// set up Express
var express = require('express');
var app = express();

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// import classes
var Event = require('./Event.js');
var Review = require('./Review.js');

/***************************************/

// endpoint for creating a new event
// this is the action of the "create new event" form
app.use('/create', (req, res) => {
    // construct the event from the form data which is in the request body
    var newEvent = new Event ({
        name: req.body.name,
        description: req.body.description,
        date: req.body.date,
        contact_name: req.body.first_name + ' ' + req.body.last_name,
        contact_email: req.body.contact_email,
        category: req.body.category, // can it be an enum?
        address: req.body.address
    });

    // save the event to the database
    newEvent.save( (err) => {
      if (err) {
        res.type('html').status(200);
        res.write('uh oh: ' + err);
        console.log(err);
        res.end();
      } else {
      // display the "successfull created" message
      res.send('successfully added ' + newEvent.name + ' to the database');
        }
      } ); 
} );


// endpoint for showing all the events
app.use('/all', (req, res) => {

    // find all the Event objects in the database
    Event.find( {}, (err, events) => {
        if (err) {
            res.type('html').status(200);
            console.log('uh oh' + err);
            res.write(err);
        } else {
            if (events.length == 0) {
                res.type('html').status(200);
                res.write('There are no events');
                res.end();
                return;
            } else {
                res.type('html').status(200);
                res.write('Here are the events in the database:');
                res.write('<ul>');
                // show all the events
                events.forEach( (event) => {
                    res.write('<li>');
                    res.write('Event Name: ' + event.name + '; description: ' + event.description);
                    // this creates a link to the /delete endpoint
                    // res.write(" <a href=\"/delete?name=" + person.name + "\">[Delete]</a>");
                    res.write('</li>');

                    });
                res.write('</ul>');
                res.end();
            }
        }
    }).sort({ 'name': 'asc' }); // this sorts them BEFORE rendering the results
});

/*
// IMPLEMENT THIS ENDPOINT!
app.use('/delete', (req, res) => {
var filter = {'name' : req.query.name};                                        
Event.findOneAndDelete (filter, (err, event) => {                            
if (err) {                                                                   
console.log(err);                                                          
} else if (!event) {                                                        
console.log("Cannot find event");                                         
} else {                                                                     
console.log("Success.");                                                   
}                                                                            
});                                                                            
res.redirect('/all');
});



// endpoint for accessing data via the web api
// to use this, make a request for /api to get an array of all Person objects
// or /api?name=[whatever] to get a single object
app.use('/api', (req, res) => {

// construct the query object
var queryObject = {};
if (req.query.name) {
// if there's a name in the query parameter, use it here
queryObject = { "name" : req.query.name };
}

Person.find( queryObject, (err, persons) => {
console.log(persons);
if (err) {
console.log('uh oh' + err);
res.json({});
}
else if (persons.length == 0) {
// no objects found, so send back empty json
res.json({});
}
else if (persons.length == 1 ) {
var person = persons[0];
// send back a single JSON object
res.json( { "name" : person.name , "age" : person.age } );
}
else {
// construct an array out of the result
var returnArray = [];
persons.forEach( (person) => {
returnArray.push( { "name" : person.name, "age" : person.age } );
});
// send it back as JSON Array
res.json(returnArray); 
}

});
});

 */


/*************************************************/

app.use('/public', express.static('public'));

app.use('/', (req, res) => { res.redirect('/public/adminhome.html'); } );

app.listen(3000,  () => {
    console.log('Listening on port 3000');
});
