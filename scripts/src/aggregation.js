db.geeks.aggregate([
    {
        $geoNear: {
            near: {
                type: "Point",
                coordinates: [-1.150086999999985, 46.151795]
            },
            distanceField: "distance",
            spherical: true,
            maxDistance: 500000
        }
    },
    {
        $project: {
            likes: 1,
            nom: 1,
            distance: 1,
            _id: 0
        }
    },
    {
        $unwind: "$likes"
    },
    {
        $group: {
            _id: "$likes",
            total: {
                $sum: 1
            },
            friends: {
                $push: {
                    nom: "$nom",
                    distance: "$distance"
                }
            }
        }
    }
]);