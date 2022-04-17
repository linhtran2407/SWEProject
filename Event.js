var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
// mongoose.connect('mongodb://localhost:27017/appDatabase');

// mongoose.createConnection('mongodb+srv://<linhtran2407>:<myproject123>@cluster0.qultw.mongodb.net/admin?retryWrites=true&w=majority');

var Schema = mongoose.Schema;

var eventSchema = new Schema({
	name: {type: String, required: true, unique: true},
    signups: {type: Array, "default": []},
	description: String,
    date: {type: Date, "default": "unknown"},
    contact_name: String,
    email: String,
    category: {type: Array, "default": []}, // can it be an enum? Linh: yeah i think it better be an enum
    address: String
});

// export eventSchema as a class called Event
module.exports = mongoose.model('Event', eventSchema);

eventSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}
