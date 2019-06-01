import requests 
import json
import data
import time
from subprocess import check_call

base_url = 'http://localhost:3000'

def firebaseTest():
	user_registration = base_url + '/users/register'

	pass1 = requests.post(user_registration, json=data.passenger1)
	assert(pass1.status_code == 200)
	pass1 = pass1.json()
	printPassengerInfo(1, pass1, data.passenger1)

	print("USUARIO UPDATEA SU TOKEN")
	token = { "token" : "abc123" }
	accept_status = requests.put(base_url + '/users/firebase', headers={'Authorization': 'Bearer ' + pass1['token']}, json=token)
	assert(accept_status.status_code == 200)

	time.sleep(1)


	updatedUser = requests.get(base_url + '/users', headers={'Authorization': 'Bearer ' + pass1['token']})

	print("USUARIO UPDATEADO {}".format(updatedUser.json()))


def printPassengerInfo(n, created, user):
	printUserInfo(n,"PASAJERO", created, user)

def printUserInfo(n, userType, created, user):
	print("{} {}: {} {} - ID: {} - UBICACION {}".format(userType, n, user["firstName"], user["lastName"], created['user']['id'], data.positions[user["position"]]))
	
firebaseTest()


