var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://localhost:27017/appDatabase');

var Schema = mongoose.Schema;

var reviewSchema = new Schema({
	body: {type: String, required: true, unique: true},
	id: {type: Number, required: true, unique: true}
});

// export reviewSchema as a class called Review
module.exports = mongoose.model('Review', reviewSchema);

reviewSchema.methods.standardizeBody = function() {
    this.body = this.body.toLowerCase();
    return this.body;
}