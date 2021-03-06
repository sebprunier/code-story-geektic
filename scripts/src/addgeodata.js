/*jslint node: true, vars: true, indent: 4, nomen: true*/
'use strict';
// require
var MongoClient = require('mongodb').MongoClient,
    gm = require('googlemaps'),
    async = require('async');

// mongodb uri
var uri = require('../conf/conf').MONGO_URL;

var exitWithError = function (err) {
    console.log('Something went wrong !');
    console.log(err);
    process.exit(1);
};

var geeksCollection;

var addGeoData = function (geek, callback) {
    if (geek.ville && geek.ville !== '' && !geek.location) {
        gm.geocode(geek.ville, function (err, cities) {
            if (err) {
                exitWithError(err);
            }
            if (cities.results && cities.results.length > 0) {
                var geodata = cities.results[0];
                var location = { type: "Point",
                    coordinates: [ geodata.geometry.location.lng, geodata.geometry.location.lat ]
                    };
                geeksCollection.update({'_id': geek._id}, { $set: {'location': location}}, { upsert: true }, function (err, data) {
                    if (err) {
                        exitWithError(err);
                    }
                    console.log('Geek %s updated !', geek.nom);
                    callback();
                });
            } else {
                callback();
            }
        });
    } else {
        callback();
    }
};

MongoClient.connect(uri, function (err, db) {
    if (err) {
        exitWithError(err);
    }
    db.collection('geeks', function (err, collection) {
        if (err) {
            exitWithError(err);
        }
        geeksCollection = collection;
        geeksCollection.find().toArray(function (err, geeks) {
            async.eachSeries(geeks, addGeoData, function (err) {
                if (err) {
                    exitWithError(err);
                }
                console.log('Done !');
                process.exit(0);
            });
        });
    });
});
