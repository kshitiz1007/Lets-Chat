
//------THIS IS CODE FOR SENDING NOTIFICATION ON NEW FRIEND REQUEST--------
	'use strict'

	const functions = require('firebase-functions');
	const admin=require('firebase-admin');
	admin.initializeApp(functions.config().firebase);

	exports.sendNotification =functions.database.
	ref('/notifications/{user_id}/{notification_id}').onWrite(event => {
	//--user at which friend request is sent--
		const user_id=event.params.user_id;
		const notification_id=event.params.notification_id;
		console.log('We have a notification to send to : ', user_id);
		if(!event.data.val()){

			return console.log('A notification has been deleted from database : ', notification_id);

		}

		const fromUser = admin.database().
		ref(`/notifications/${user_id}/${notification_id}`).once('value'); 
		return fromUser.then(fromUserResult => {
 
			const from_user_id = fromUserResult.val().from;
			console.log('You have new notification from : ', from_user_id);

			const userQuery = admin.database().
			ref(`users/${from_user_id}/name`).once('value');

			const deviceToken = admin.database().
			ref(`/users/${user_id}/device_token`).once('value');

			return Promise.all([userQuery,deviceToken]).then(result =>{
				const userName = result[0].val();
				const token_id = result[1].val();

				const payload = {
					notification: {
						title: "New Friend Request",
						body : `${userName} has sent you friend request`,
						icon: "default",
						click_action : "com.example.singhkshitiz.letschat_TARGET_NOTIFICATION"
					},
					data : {
						from_user_id : from_user_id
					} 
				};

				return admin.messaging().sendToDevice(token_id,payload).then(response =>{

					console.log('This was the notification feature');
					return ;
				});
			});

		});

	});

