import { Routes, Route } from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import ProfilePage from "./pages/ProfilePage";
import FriendsPage from "./pages/FriendsPage";
import AppLayout from "./components/ui/AppLayout";

const App = () => {
  const user = {
    name: "John Doe",
    email: "john.doe@example.com",
    avatar: "https://bit.ly/dan-abramov",
  };

  return (
    <>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/profile" element={<ProfilePage user={user} />} />
          <Route path="/friends" element={<FriendsPage />} />
        </Route>
      </Routes>
    </>
  );
};

export default App;
