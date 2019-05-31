import requests 
import json
import data
import time
from subprocess import check_call

base_url = 'http://localhost:3000'
create_trip = base_url + '/trips'
user_registration = base_url + '/users/register'


drivers = []
passengers = []
trip = {};

def createUsers():
	driver1 = requests.post(user_registration, json=data.driver2) #because driver 1 is not elegible
	assert(driver1.status_code == 200)
	driver1 = driver1.json()
	drivers.append(driver1)
	printDriverInfo(1, driver1, data.driver2)

	driver_data = data.driver1
	driver_data["position"] = data.driver2["position"]
	driver2 = requests.post(user_registration, json=driver_data)
	assert(driver2.status_code == 200)
	drivers.append(driver2.json())
	printDriverInfo(2, drivers[1], driver_data)
	

	driver_data = data.driver3
	driver_data["position"] = data.driver2["position"]
	driver3 = requests.post(user_registration, json=driver_data)
	assert(driver3.status_code == 200)
	drivers.append(driver3.json())
	printDriverInfo(3, drivers[0], driver_data)

	pass1 = requests.post(user_registration, json=data.passenger1)
	assert(pass1.status_code == 200)
	passengers.append(pass1.json())
	printPassengerInfo(1, passengers[0], data.passenger1)

	printSeparator()


def createTrips():
	#trips = []
	review = data.review
	global trip
	total_trips = 11
	print("CREANDO  REVIEWS PARA CONDUCTOR 1...")
	trip = requests.post(create_trip, json=data.trip1)
	assert(trip.status_code == 200)
	trip = trip.json()
	#print(json.dumps(trip))
	#trips.append(trip.json())

	review['userId'] = drivers[0]['user']['id']
	review['tripId'] = trip['id']
	review['review']['stars'] = 5

	for i in range(total_trips):
		requests.post(base_url + '/users/review', headers={'Authorization': 'Bearer ' + passengers[0]['token']}, json=review)

	print("CREANDO REVIEWS PARA CONDUCTOR 2...")
	total_trips = 2

	review['userId'] = drivers[1]['user']['id']
	review['tripId'] = trip['id']
	review['review']['stars'] = 3

	for i in range(total_trips):
		r = requests.post(base_url + '/users/review', headers={'Authorization': 'Bearer ' + passengers[0]['token']}, json=review)
		assert(r.status_code == 200)

	print("CREANDO REVIEWS PARA CONDUCTOR 3...")
	total_trips = 3

	review['userId'] = drivers[2]['user']['id']
	review['tripId'] = trip['id']
	review['review']['stars'] = 4

	for i in range(total_trips):
		r = requests.post(base_url + '/users/review', headers={'Authorization': 'Bearer ' + passengers[0]['token']}, json=review)
		assert(r.status_code == 200)

	printSeparator()

def getDriverScores():

	for driver in drivers:
		reject_status = requests.get(base_url + '/users/' + driver['user']['id'] + '/rating', headers={'Authorization': 'Bearer ' + driver['token']})
		score = reject_status.json()
		print("CALIFICACIONES DEL CONDUCTOR {} {}:\n CANTIDAD {} \n PROMEDIO {}".format(driver['user']['firstName'], driver['user']['lastName'],score['count'], float(score['sum']) / float(score['count'])))


def selectDriver():
	global trip
	print("USUARIO ACEPTA EL VIAJE")
	accept = { "status" : "2" }
	accept_status = requests.put(create_trip + '/' + trip['id'], json=accept)
	assert(accept_status.status_code == 200)

	time.sleep(1)

	printSeparator()

	#time.sleep(2)

	for driver in drivers[::-1]:
		time.sleep(1)
		trips_driver = requests.get(base_url + '/users/drivers/pendingTrips', headers={'Authorization': 'Bearer ' + driver['token']})

		while(not trips_driver):
			print(trips_driver)
			print("ESPERANDO A QUE SE ASIGNE EL VIAJE AL CONDUCTOR {} {}.".format(driver['user']['firstName'], driver['user']['lastName']))
			time.sleep(3)
			trips_driver = requests.get(base_url + '/users/drivers/pendingTrips', headers={'Authorization': 'Bearer ' + driver['token']})

		print("CONDUCTOR {} {} RECHAZA EL VIAJE".format(driver['user']['firstName'], driver['user']['lastName']))
		reject_status = requests.post(create_trip + '/' + trip['id'] + '/reject', headers={'Authorization': 'Bearer ' + driver['token']})

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

def testOrderRatings():
	createUsers()
	createTrips()
	getDriverScores()
	selectDriver()
	getDriverScores()


#resetServer()
testOrderRatings()

