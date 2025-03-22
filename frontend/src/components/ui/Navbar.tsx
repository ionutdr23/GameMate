import { Menu } from "antd";
import { Link } from "react-router-dom";

const Navbar = () => {
  return (
    <Menu mode="horizontal" theme="dark">
      <Menu.Item key="home">
        <Link to="/">Home</Link>
      </Menu.Item>
      <Menu.Item key="profile">
        <Link to="/profile">Profile</Link>
      </Menu.Item>
      <Menu.Item key="friends">
        <Link to="/friends">Friends</Link>
      </Menu.Item>
      <Menu.Item key="login" style={{ marginLeft: "auto" }}>
        <Link to="/login">Login</Link>
      </Menu.Item>
      <Menu.Item key="register">
        <Link to="/register">Register</Link>
      </Menu.Item>
    </Menu>
  );
};

export default Navbar;
