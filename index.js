// set up Express
var express = require('express');
var app = express();
app.set('view engine', 'ejs');
const mongoose = require('mongoose');

// connect to Atlas
const uri = "mongodb://linhtran2407:myproject123@cluster0-shard-00-00.qultw.mongodb.net:27017,cluster0-shard-00-01.qultw.mongodb.net:27017,cluster0-shard-00-02.qultw.mongodb.net:27017/test?ssl=true&replicaSet=atlas-9wpch9-shard-0&authSource=admin&retryWrites=true&w=majority'"
mongoose.connect(uri)
.then(() => console.log("Database connection successfull")).catch(() => console.log("Database connection failed"));

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
    console.log("body is here")
    console.log(req.body)
    var newEvent = new Event ({
        name: req.body.name,
        description: req.body.description,
        date: req.body.date,
        contact_name: req.body.first_name + ' ' + req.body.last_name,
        email: req.body.email,
        category: req.body.category, // can it be an enum?
        address: req.body.address,
        approved: false
    });

    // save the event to the database
    newEvent.save( (err) => {
      if (err) {
        res.type('html').status(200);
        res.write('uh oh otay: ' + err);
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
                    res.write("<a href=\"/view_event?id=" + event._id + "\">[View]</a>");
                    res.write(" <a href=\"/show_editForm?id=" + event._id + "\">[Edit]</a>");
                    res.write(" <a href=\"/delete_event1?name=" + event.name + "\">[Delete]</a>");
                    res.write('</li>');
                });
                res.write('</ul>');
                res.end();
            }
        }
    }).sort({ 'name': 'asc' }); // this sorts them BEFORE rendering the results
});

// endpoint for showing all the events
app.use('/allapp', (req, res) => {
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
                var returnArray = [];
                // show all the events
                events.forEach( (event) => {
                    var eventObject = {
                    "name": event.name,
                    "description": event.description,
                    "attendees": event.signups,
                    "date" : event.date,
                    "contact_name" : event.contact_name,
                    "email" : event.email,
                    "category" : event.category
                };
                returnArray.push( eventObject );
                });
                res.json(returnArray); 
                res.end();
            }
        }
    }).sort({ 'name': 'asc' }); // this sorts them BEFORE rendering the results
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
            var categories=event.category.join(", ");
            var date = ''
            if (event.date) {
                date = event.date.toLocaleDateString('en-US', {timeZone: 'UTC'})
            }
            console.log(categories);
            res.type('html').status(200);
            res.write("<span style='font-weight:bold'> Event Information </span> <br/>");
            res.write('Name: ' + event.name + '<br/> Description: ' + event.description 
            + '<br/> List of attendees: ' + event.signups + '<br/> Posted: ' + date
            + '<br/> Organizer name: ' + event.contact_name + '<br/> Organizer email: ' + event.email
            + '<br/> Category: ' + categories + '<br/> Location: ' + event.address + '<br/>');
            res.write(" <a href=\"/all" + "\">[Back to list of events]</a>");
            res.write(" <a href=\"/delete_event1?name=" + event.name + "\">[Delete]</a>" + "<br/>");
            if (!event.approved) {
                res.write("This event is not approved!");
                res.write(" <a href=\"/approve?id=" + event._id + "\">[Approve]</a>");
            } else {
                res.write("This event is approved!");
            }
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

app.use('/edit_event', (req, res) => {
    var filter = {'_id' : req.body.id};
    var action = { '$set' : {
        'name' : req.body.name,
        'description' : req.body.description},
        'contact_name' : req.body.contact_name,
        'email' : req.body.contact_email,
        'address' : req.body.address    
    }
	Event.findOneAndUpdate (filter, action, (err, orig) => {
        if (err) {
            res.json ({"status" : err});
        } else if (!orig) {
            res.json ({"status" : "no event found"})
        } else {
            console.log("Successfully update event %s", req.body.id)
            res.redirect('/view_event?id=' + req.body.id)
        }
    })
});


/***************************************/


app.use('/delete_event1', (req, res) => {
    var filter = {'name' : req.query.name};
	Event.findOne (filter, (err, event) => {
		if (err) {
			console.log(err);
		} else if (!event) {
			console.log("Cannot find event.");
		} else {
			//console.log("Successfully found event %s", req.query.name);
            var categories=event.category.join(", ");
            var date = ''
            if (event.date) {
                date = event.date.toLocaleDateString('en-US', {timeZone: 'UTC'})
            }
            res.type('html').status(200);
            res.write("<span style='font-weight:bold'> Event Information </span> <br/>");
            res.write('Name: ' + event.name + '<br/> Description: ' + event.description 
            + '<br/> List of attendees: ' + event.signups + '<br/> Posted: ' + date
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
            res.redirect('/all') 
        }
    });
});

// endpoint for showing all the reviews
app.use('/reviews', (req, res) => {
    // find all the Review objects in the database
    Review.find( {}, (err, reviews) => {
        if (err) {
            res.type('html').status(200);
            console.log('uh oh' + err);
            res.write(err);
        } else {
            if (reviews.length == 0) {
                res.type('html').status(200);
                res.write('There are no events');
                res.end();
            } else {
                res.type('html').status(200);
                res.write('Here are the reviews in the database:');
                res.write('<ul>');
                // show all the reviews
                reviews.forEach( (review) => {
                    res.write('<li>');
                    res.write('Event Title: ' + review.title + '<br/>');
                    res.write('Review description: ' + reviwe.body + '<br/>');
                    res.write(" <a href=\"/delete_review1?name=" + review.title + "\">[Delete]</a>");
                    res.write('</li>');
                });
                res.write('</ul>');
                res.end();
            }
        }
    }).sort({ 'name': 'asc' }); // this sorts them BEFORE rendering the results
});

app.use('/delete_review1', (req, res) => {
    var filter = {'id' : req.query.id};
	Event.findOne (filter, (err, review) => {
		if (err) {
			console.log(err);
		} else if (!review) {
			console.log("Cannot find event.");
		} else {
			console.log("Successfully found review %s", req.query.name);
            res.type('html').status(200);
            res.write("<span style='font-weight:bold'> Review Information </span> <br/>");
            res.write('Name: ' + review.title + '<br/>'
            + '<br/> Description: ' + review.body);
            res.write(" <a href=\"/delete_review?name=" + review.id + "\">[Confirm Deletion]</a>");
            res.end();
		}
	});
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



// endpoint for approving a new event
app.use('/approve', (req, res) => {
	var filter = {'_id' : req.query.id};
    var action = { '$set' : {'approved' : true}};
	Event.findOneAndUpdate (filter, action, (err, orig) => {
		if (err) {
			console.log(err);
		} else if (!orig) {
			console.log("Cannot find event.");
		} else {
            console.log("Successfully approve the event.");
            res.redirect('/view_event?id=' + req.query.id);
		}
    });

} );

// app.use('/delete_eventByID', (req, res) => {
//     var filter = {'_id' : req.query.id};
//     Event.findOneAndDelete (filter, (err, event) => {
//         if (err) {
//             console.log(err);
//         } else if (!event) {
//             console.log("Cannot find event.");
//         } else {
//             console.log("Success deleting even by id.");
//             res.redirect('/all') 
//         }
//     });
// })

/*************************************************/

app.use('/public', express.static('public'));

app.use('/', (req, res) => { res.redirect('/public/adminhome.html'); } );

app.listen(3000,  () => {
    console.log('Listening on port 3000');
});
