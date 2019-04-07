import axios from "axios";

const googleMapsApiKey = "AIzaSyAmj1uQ_CG2WHbrUZs90Cmw7ZUrglIKQWM";

/**
 * Returns geographic coordinates based on an address
 * For more information, see https://developers.google.com/maps/documentation/geocoding/intro
 * @param address A phisical address (e.g. `"1600 Amphitheatre Parkway, Mountain View, CA"`)
 * @returns The geographic coordinates of the address, in latitude and longitude (e.g. `"37.423021, -122.083739"`).
 */
async function getGeocode(address: string): Promise<string> {
  const url = "https://maps.googleapis.com/maps/api/geocode/json";
  const encodedAddress = encodeURI(address);

  const result = await axios.get(`${url}?&region=ar&country=AR&key=${googleMapsApiKey}&address=${encodedAddress}`);

  if (result.status !== 200) {
    throw new Error(result.statusText);
  }

  if (!result.data || !result.data) {
    throw new Error("Google API didn't return data when requesting coordinates");
  }

  if (result.data.status.toUpperCase() !== "OK") {
    throw new Error(`Received the following error from Google API: "${result.data.status}" ${result.data.error_message}`);
  }

  const geolocation = result.data.results[0].geometry.location;
  return `${geolocation.lat},${geolocation.lng}`;
}

/**
 * Returns directions between an origin an destination
 * For more information, see https://developers.google.com/maps/documentation/geocoding/intro
 * @param origin An origin as an address or lat/long (e.g. `"1600 Amphitheatre Parkway, Mountain View, CA"`)
 * @param destination A destination as an address or lat/long (e.g. `"1600 Amphitheatre Parkway, Mountain View, CA"`)
 * @returns The geographic coordinates of the address, in latitude and longitude (e.g. `"37.423021, -122.083739"`).
 */
async function getDirections(origin: string, destination: string): Promise<string> {
  const url = "https://maps.googleapis.com/maps/api/directions/json";
  const encodedOrigin = encodeURI(origin);
  const encodedDestination = encodeURI(destination);

  const result = await axios.get(
    `${url}?&region=ar&country=AR&key=${googleMapsApiKey}&origin=${encodedOrigin}&destination=${encodedDestination}`
  );

  if (result.status !== 200) {
    throw new Error(result.statusText);
  }

  if (!result.data || !result.data) {
    throw new Error("Google API didn't return data when requesting coordinates");
  }

  if (result.data.status.toUpperCase() !== "OK") {
    throw new Error(`Received the following error from Google API: "${result.data.status}" ${result.data.error_message}`);
  }

  return JSON.stringify(result.data.routes);
}

export default {
  getGeocode,
  getDirections
};
