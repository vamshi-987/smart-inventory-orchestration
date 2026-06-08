import { useState } from "react";
import { MapContainer, TileLayer, Marker, useMapEvents, useMap } from "react-leaflet";
import L from "leaflet";

const markerIcon = new L.Icon({
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
  iconSize: [25, 41],
  iconAnchor: [12, 41],
});

function ChangeMapView({ position }) {
  const map = useMap();

  if (position) {
    map.setView(position, 14);
  }

  return null;
}

function LocationMarker({ position, onSelect }) {
  useMapEvents({
    click(e) {
      onSelect(e.latlng.lat, e.latlng.lng);
    },
  });

  if (!position) return null;

  return <Marker position={position} icon={markerIcon} />;
}

export default function LocationPicker({ onLocationConfirm }) {
  const [address, setAddress] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [position, setPosition] = useState([17.385, 78.4867]);
  const [selectedLocation, setSelectedLocation] = useState(null);
  const [loading, setLoading] = useState(false);

  async function searchAddress(query) {
    if (!query.trim()) return;

    setLoading(true);

    const response = await fetch(
      `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(
        query
      )}&addressdetails=1&limit=5`
    );

    const data = await response.json();

    const results = data.map((item) => ({
      formattedAddress: item.display_name,
      latitude: Number(item.lat),
      longitude: Number(item.lon),
      city:
        item.address.city ||
        item.address.town ||
        item.address.village ||
        item.address.state_district ||
        item.address.county ||
        "",
      pincode: item.address.postcode || "",
    }));

    setSuggestions(results);
    setLoading(false);
  }

  async function reverseGeocode(lat, lng) {
    setLoading(true);

    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&addressdetails=1`
    );

    const data = await response.json();

    const location = {
      formattedAddress: data.display_name,
      latitude: lat,
      longitude: lng,
      city:
        data.address.city ||
        data.address.town ||
        data.address.village ||
        data.address.state_district ||
        data.address.county ||
        "",
      pincode: data.address.postcode || "",
    };

    setSelectedLocation(location);
    setAddress(location.formattedAddress);
    setPosition([lat, lng]);
    setLoading(false);
  }

  function handleSuggestionClick(location) {
    setSelectedLocation(location);
    setAddress(location.formattedAddress);
    setPosition([location.latitude, location.longitude]);
    setSuggestions([]);
  }

  function confirmLocation() {
    if (!selectedLocation) {
      alert("Please select a location first");
      return;
    }

    onLocationConfirm(selectedLocation);
  }

  return (
    <div className="location-picker">
      <label>Search Address</label>

      <div className="location-search-row">
        <input
          value={address}
          placeholder="Search address, area, city, pincode..."
          onChange={(e) => setAddress(e.target.value)}
        />

        <button type="button" onClick={() => searchAddress(address)}>
          Search
        </button>
      </div>

      {loading && <p>Loading location...</p>}

      {suggestions.length > 0 && (
        <div className="suggestions">
          {suggestions.map((location, index) => (
            <button
              type="button"
              key={index}
              onClick={() => handleSuggestionClick(location)}
            >
              {location.formattedAddress}
            </button>
          ))}
        </div>
      )}

      <div className="map-box">
        <MapContainer center={position} zoom={13} style={{ height: "350px", width: "100%" }}>
          <TileLayer
            attribution='&copy; OpenStreetMap contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          <ChangeMapView position={position} />

          <LocationMarker
            position={selectedLocation ? position : null}
            onSelect={reverseGeocode}
          />
        </MapContainer>
      </div>

      {selectedLocation && (
        <div className="selected-location">
          <h4>Selected Location</h4>
          <p>{selectedLocation.formattedAddress}</p>
          <p>City: {selectedLocation.city || "Not found"}</p>
          <p>Pincode: {selectedLocation.pincode || "Not found"}</p>
          <p>
            Lat/Lng: {selectedLocation.latitude}, {selectedLocation.longitude}
          </p>

          <button type="button" onClick={confirmLocation}>
            Confirm Location
          </button>
        </div>
      )}
    </div>
  );
}