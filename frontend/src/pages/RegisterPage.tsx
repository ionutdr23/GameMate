import { Form, Input, Button, Typography } from "antd";

const { Title } = Typography;

interface RegisterFormValues {
  username: string;
  email: string;
  password: string;
}

const RegisterPage = () => {
  const onFinish = (values: RegisterFormValues) => {
    console.log("Registration details:", values);
  };

  return (
    <div style={{ maxWidth: "400px", margin: "50px auto" }}>
      <Title level={2}>Register</Title>
      <Form name="register" onFinish={onFinish}>
        <Form.Item
          name="username"
          rules={[{ required: true, message: "Please enter your username!" }]}
        >
          <Input placeholder="Username" />
        </Form.Item>
        <Form.Item
          name="email"
          rules={[{ required: true, message: "Please enter your email!" }]}
        >
          <Input placeholder="Email" />
        </Form.Item>
        <Form.Item
          name="password"
          rules={[{ required: true, message: "Please enter your password!" }]}
        >
          <Input.Password placeholder="Password" />
        </Form.Item>
        <Button type="primary" htmlType="submit" block>
          Register
        </Button>
      </Form>
    </div>
  );
};

export default RegisterPage;
