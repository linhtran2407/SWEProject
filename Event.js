var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://localhost:27017/appDatabase');

var Schema = mongoose.Schema;

var eventSchema = new Schema({
	name: {type: String, required: true, unique: true},
    signups: {type: Array, "default": []},
	description: String,
    date: Date,
    contact_name: String,
    contact_email: String,
    category: String, // can it be an enum? Linh: yeah i think it better be an enum
    address: String
});

// export eventSchema as a class called Event
module.exports = mongoose.model('Event', eventSchema);

eventSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}
