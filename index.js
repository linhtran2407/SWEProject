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
        email: req.body.email,
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
            } else {
                res.type('html').status(200);
                res.write('Here are the events in the database:');
                res.write('<ul>');
                // show all the events
                events.forEach( (event) => {
                    res.write('<li>');
                    res.write('Event Name: ' + event.name + '<br/>');
                    res.write('Event description: ' + event.description + '<br/>');
                    // this creates a link to the /view_event and /edit_event endpoints
                    res.write("<a href=\"/view_event?name=" + event.name + "\">[View]</a>");
                    res.write(" <a href=\"/edit_event?name=" + event.name + "\">[Edit]</a>");
                    res.write(" <a href=\"/delete_event1?name=" + event.name + "\">[Delete]</a>");
                    res.write('</li>');
                });
                res.write('</ul>');
                res.end();
            }
        }
    }).sort({ 'name': 'asc' }); // this sorts them BEFORE rendering the results
});

// endpoint for viewing 1 event
app.use('/view_event', (req, res) => {
	var filter = {'name' : req.query.name};
	Event.findOne (filter, (err, event) => {
		if (err) {
			console.log(err);
		} else if (!event) {
			console.log("Cannot find event.");
		} else {
			console.log("Successfully find event %s", req.query.name);
            var categories=event.category.join(", ");
            console.log(categories);
            res.type('html').status(200);
            res.write("<span style='font-weight:bold'> Event Information </span> <br/>");
            res.write('Name: ' + event.name + '<br/> Description: ' + event.description 
            + '<br/> List of attendees: ' + event.signups + '<br/> Posted: ' + event.date.toLocaleDateString("en-US")
            + '<br/> Organizer name: ' + event.contact_name + '<br/> Organizer email: ' + event.email
            + '<br/> Category: ' + categories + '<br/> Location: ' + event.address + '<br/>');
            res.write(" <a href=\"/delete_event1?name=" + event.name + "\">[Delete]</a>");
            res.end();
		}
	});
});

// endpoint for viewing 1 event
app.use('/view_event', (req, res) => {
	var filter = {'_id' : req.query.id};
	Event.findOne (filter, (err, event) => {
		if (err) {
			console.log(err);
		} else if (!event) {
			console.log("Cannot find event.");
		} else {
			console.log("Successfully find event %s", req.query.id);
            res.type('html').status(200);
            res.write("<span style='font-weight:bold'> Event Information </span> <br/>");
            res.write('Name: ' + event.name + '<br/> Description: ' + event.description 
            + '<br/> List of attendees: ' + event.signups + '<br/> Posted: ' + event.date 
            + '<br/> Organizer name: ' + event.contact_name + '<br/> Organizer email: ' + event.contact_email
            + '<br/> Category: ' + event.category + '<br/> Location: ' + event.address + '<br/>');
            res.write(" <a href=\"/all" + "\">[Back to list of events]</a>");
            res.write(" <a href=\"/delete_event1?name=" + event.name + "\">[Delete]</a>");
            res.end();
		}
	});
});

// endpoint for editing 1 event
app.use('/show_editForm', (req, res) => {
	var query = {"_id" : req.query.id };
    
	Event.findOne( query, (err, result) => {
		if (err) {
		    res.render("error", {'error' : err});
		} else {
		    // this uses EJS to render the views/editForm.ejs template	
		    res.render("editForm", {"event" : result});
		}
	});
});

app.use('/delete_event1', (req, res) => {
    var filter = {'name' : req.query.name};
	Event.findOne (filter, (err, event) => {
		if (err) {
			console.log(err);
		} else if (!event) {
			console.log("Cannot find event.");
		} else {
			console.log("Successfully found event %s", req.query.name);
            var categories=event.category.join(", ");
            res.type('html').status(200);
            res.write("<span style='font-weight:bold'> Event Information </span> <br/>");
            res.write('Name: ' + event.name + '<br/> Description: ' + event.description 
            + '<br/> List of attendees: ' + event.signups + '<br/> Posted: ' + event.date.toLocaleDateString("en-US")
            + '<br/> Organizer name: ' + event.contact_name + '<br/> Organizer email: ' + event.email
            + '<br/> Category: ' + categories + '<br/> Location: ' + event.address + '<br/>');
            res.write(" <a href=\"/delete_event?name=" + event.name + "\">[Confirm Deletion]</a>");
            res.end();
		}
	});
});

app.use('/delete_event', (req, res) => {
    var filter = {'name' : req.query.name};
    Event.findOneAndDelete (filter, (err, event) => {
        if (err) {
            console.log(err);
        } else if (!event) {
            console.log("Cannot find event.");
        } else {
            console.log("Success.");
        }
    });
    res.redirect('/all') 
});

app.use('/delete_review', (req, res) => {
    var filter = {'id' : req.query.id};
	Event.findOneAndDelete (filter, (err, review) => {
		if (err) {
			console.log(err);
		} else if (!review) {
			console.log("Cannot find review.");
		} else {
			console.log("Success.");
		}
	});
	res.redirect('/all');
});


/*************************************************/

app.use('/public', express.static('public'));

app.use('/', (req, res) => { res.redirect('/public/adminhome.html'); } );

app.listen(3000,  () => {
    console.log('Listening on port 3000');
});
