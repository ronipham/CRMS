var cordova = require('cordova');

var roms = {
	initROMS : function(successCallback, errorCallback, params) {
		cordova.exec(successCallback, errorCallback, "RomsPushPlugin", "InitROMS", [params]);
	},

	pushMessage : function(successCallback, errorCallback, params) {
		cordova.exec(successCallback, errorCallback, "RomsPushPlugin", "PushMessage", [params]);
	}
};

module.exports = roms;
