/*jslint node: true, vars: true, indent: 4, nomen: true, plusplus: true*/
'use strict';
// require
var MongoClient = require('mongodb').MongoClient,
    async = require('async'),
    _ = require('underscore'),
    faker = require('Faker');

var randomGeeks = process.argv[2];

// mongodb uri
var uri = require('../conf/conf').MONGO_URL;

// initial data
var staticGeeks = require('../../web/geeks.json');

var exitWithError = function (err) {
    console.log('Something went wrong !');
    console.log(err);
    process.exit(1);
};

function randomImage() {
    var randomGeekNumber = Math.floor(Math.random() * 8);
    return 'static/GIT_HASH/img/geek' + randomGeekNumber + '.jpg';
}

MongoClient.connect(uri, function (err, db) {
    if (err) {
        exitWithError(err);
    }
    db.collection('geeks', function (err, collection) {
        if (err) {
            exitWithError(err);
        }
        async.series(
            [
                // 1- remove the collection (if existing)
                function (callback) {
                    collection.remove({}, function (err, removed) {
                        callback(err, removed + " geek(s) removed !");
                    });
                },

                // 2- insert geeks !
                function (callback) {
                    async.parallel([
                        function (callback) {
                            // transform geeks for mongo
                            var geeks = _.map(staticGeeks, function (geek) {
                                return _.extend({'nom': geek.prenom + ' ' + geek.nom},
                                                _.pick(geek, 'ville', 'likes'),
                                                {'imageUrl': randomImage()});
                            });
                            // insert them
                            collection.insert(geeks, {safe: true}, function (err, result) {
                                callback(err, result.length + " geek(s) inserted !");
                            });
                        },
                        function (callback) {
                            var i;
                            if (randomGeeks) {
                                var geeks = [];
                                // create geeks
                                for (i = 0; i < randomGeeks; i++) {
                                    geeks.push({
                                        'nom': faker.Name.findName(),
                                        'ville' : faker.Address.city(),
                                        'likes' : faker.Helpers.shuffle([
                                            faker.random.bs_buzz(),
                                            faker.random.bs_adjective(),
                                            faker.random.bs_noun()]),
                                        'imageUrl': randomImage(),
                                        'location': {
                                            'type' : 'Point',
                                            'coordinates': [+faker.Address.longitude(), +faker.Address.latitude() ]
                                        }
                                    });
                                }
                                // insert them
                                collection.insert(geeks, {safe: true}, function (err, result) {
                                    callback(err, result.length + " random geek(s) inserted !");
                                });
                            } else {
                                callback(null, 'no random geeks added.');
                            }
                        }
                    ], callback);
                }
            ],
            // final callback function
            function (err, results) {
                if (err) {
                    exitWithError(err);
                }
                results.map(function (result) {
                    var str = _.isArray(result) ? result.join('\n') : result;
                    console.log(result);
                });
                process.exit(0);
            }
        );
    });
});
