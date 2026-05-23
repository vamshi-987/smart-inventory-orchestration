import { useEffect, useState } from "react";
import api from "../api/axiosConfig";

export default function Notifications() {
  const [notifications, setNotifications] = useState([]);

  const fetchNotifications = async () => {
    const res = await api.get("/notifications");
    setNotifications(res.data);
  };

  useEffect(() => {
    let mounted = true;
    const load = async () => {
      const res = await api.get("/notifications");
      if (!mounted) return;
      setNotifications(res.data);
    };
    load();
    return () => {
      mounted = false;
    };
  }, []);

  const markAsRead = async (id) => {
    await api.put(`/notifications/${id}/read`);
    fetchNotifications();
  };

  return (
    <div>
      <h1>Notifications</h1>

      <table>
        <thead>
          <tr>
            <th>Type</th>
            <th>Message</th>
            <th>Read</th>
            <th>Action</th>
          </tr>
        </thead>

        <tbody>
          {notifications.map((notification) => (
            <tr key={notification.id}>
              <td>{notification.type}</td>
              <td>{notification.message}</td>
              <td>{notification.readStatus ? "Yes" : "No"}</td>
              <td>
                {!notification.readStatus && (
                  <button onClick={() => markAsRead(notification.id)}>
                    Mark as Read
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}