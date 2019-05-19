import requests 
import json
import data
import time
from subprocess import check_call

base_url = 'http://localhost:3000'


def getTrips():
	r = requests.get(base_url + '/trips')
	assert(r.status_code == 200)
	print("GET TRIPS: " + json.dumps(r.json()));

def getUsers():
	r = requests.get(base_url + '/users/all')
	assert(r.status_code == 200)
	print("GET USERS: " + json.dumps(r.json()));

def createUsers():
	create_trip = base_url + '/trips'
	user_registration = base_url + '/users/register'

	driver1 = requests.post(user_registration, json=data.driver1)
	assert(driver1.status_code == 200)
	driver1 = driver1.json()
	printDriverInfo(1, driver1, data.driver1)

	driver2 = requests.post(user_registration, json=data.driver2)
	assert(driver2.status_code == 200)
	driver2 = driver2.json()
	printDriverInfo(2, driver2, data.driver2)
	

	driver3 = requests.post(user_registration, json=data.driver3)
	assert(driver3.status_code == 200)
	driver3 = driver3.json()
	printDriverInfo(3, driver3, data.driver3)

	pass1 = requests.post(user_registration, json=data.passenger1)
	assert(pass1.status_code == 200)
	pass1 = pass1.json()
	printPassengerInfo(1, pass1, data.passenger1)

	printSeparator()

	trip1 = requests.post(create_trip, json=data.trip1)
	assert(trip1.status_code == 200)
	trip1 = trip1.json()
	printTripInfo(data.trip1)

	printSeparator()

	print("USUARIO ACEPTA EL VIAJE")
	accept = { "status" : "2" }
	accept_status = requests.put(create_trip + '/' + trip1['id'], json=accept)
	assert(accept_status.status_code == 200)

	time.sleep(1)

	printSeparator()

	time.sleep(2)

	trips_driver3 = requests.get(base_url + '/users/drivers/pendingTrips', headers={'Authorization': 'Bearer ' + driver3['token']})

	while(not trips_driver3):
		print(trips_driver3)
		print("ESPERANDO A QUE SE ASIGNE EL VIAJE AL CONDUCTOR")
		time.sleep(3)
		trips_driver3 = requests.get(base_url + '/users/drivers/pendingTrips', headers={'Authorization': 'Bearer ' + driver3['token']})

	print("CONDUCTOR {} RECHAZA EL VIAJE".format(driver3['user']['id']))
	reject_status = requests.post(create_trip + '/' + trip1['id'] + '/reject', headers={'Authorization': 'Bearer ' + driver3['token']})

	time.sleep(2)

	trips_driver2 = requests.get(base_url + '/users/drivers/pendingTrips', headers={'Authorization': 'Bearer ' + driver2['token']})

	while(not trips_driver2):
		print(trips_driver2)
		print("ESPERANDO A QUE SE ASIGNE EL VIAJE AL CONDUCTOR")
		time.sleep(3)
		trips_driver2 = requests.get(base_url + '/users/drivers/pendingTrips', headers={'Authorization': 'Bearer ' + driver2['token']})

	print("CONDUCTOR {} ACEPTA EL VIAJE".format(driver2['user']['id']))
	reject_status = requests.post(create_trip + '/' + trip1['id'] + '/accept', headers={'Authorization': 'Bearer ' + driver2['token']})

	printSeparator()
	reject_status = requests.get(base_url + '/users/' + driver3['user']['id'] + '/rating', headers={'Authorization': 'Bearer ' + driver3['token']})
	score = reject_status.json()
	print("CALIFICACION DEL CONDUCTOR QUE RECHAZA {}".format(float(score['sum']) / float(score['count'])))


def runTestCases():
	createUsers()

def printDriverInfo(n, created, user):
	printUserInfo(n,"CONDUCTOR", created, user)

def printPassengerInfo(n, created, user):
	printUserInfo(n,"PASAJERO", created, user)

def printUserInfo(n, userType, created, user):
	print("{} {}: {} {} - ID: {} - UBICACION {}".format(userType, n, user["firstName"], user["lastName"], created['user']['id'], data.positions[user["position"]]))

def printSeparator():
	print("-----------------------------------------------------------")
	print()

def printTripInfo(trip):
	print("NUEVO VIAJE DESDE: {}".format(trip["origin"]))


def resetServer():
	check_call('npm run db:rollback', shell=True)
	check_call('npm run db:migrate', shell=True)
	#check_call('npm run build', shell=True)
	#check_call('npm start', shell=True)

resetServer()
runTestCases()


