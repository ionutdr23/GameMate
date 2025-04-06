import { Form, Input, Button, Typography } from "antd";

const { Title } = Typography;

interface LoginFormValues {
  username: string;
  password: string;
}

const LoginPage = () => {
  const onFinish = (values: LoginFormValues) => {
    console.log("Login details:", values);
  };

  return (
    <div style={{ maxWidth: "400px", margin: "50px auto" }}>
      <Title level={2}>Login</Title>
      <Form name="login" onFinish={onFinish}>
        <Form.Item
          name="username"
          rules={[{ required: true, message: "Please enter your username!" }]}
        >
          <Input placeholder="Username" />
        </Form.Item>
        <Form.Item
          name="password"
          rules={[{ required: true, message: "Please enter your password!" }]}
        >
          <Input.Password placeholder="Password" />
        </Form.Item>
        <Button type="primary" htmlType="submit" block>
          Log in
        </Button>
      </Form>
    </div>
  );
};

export default LoginPage;
