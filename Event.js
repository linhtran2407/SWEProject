var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var eventSchema = new Schema({
	name: {type: String, required: true, unique: true},
    signups: {type: Array, "default": []},
	description: String,
    date: {type: Date, "default": "unknown"},
    contact_name: String,
    email: String,
    category: {type: Array, "default": []}, // can it be an enum? Linh: yeah i think it better be an enum
    address: String,
    approved: {type: Boolean}
});

// export eventSchema as a class called Event
module.exports = mongoose.model('Event', eventSchema);

eventSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}
