var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var reviewSchema = new Schema({
	body: {type: String, required: true, unique: true},
    id: {type: Number, required: true, unique: true},
    title: {type: String, required: true, unique: true}
});

// export reviewSchema as a class called Review
module.exports = mongoose.model('Review', reviewSchema);

reviewSchema.methods.standardizeBody = function() {
    this.body = this.body.toLowerCase();
    return this.body;
}
