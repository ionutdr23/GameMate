import React from "react";
import { useAuth0 } from "@auth0/auth0-react";
import { AuthButton } from "../auth/AuthButton";

const ProtectedPage: React.FC = () => {
  const { isAuthenticated, user } = useAuth0();

  if (!isAuthenticated) {
    return (
      <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
        <h1 className="text-2xl font-bold text-gray-800 mb-4">
          You are not logged in
        </h1>
        <AuthButton />
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-50 text-gray-800">
      <div className="bg-white shadow-lg rounded-lg p-8 w-11/12 max-w-md">
        <h1 className="text-3xl font-bold mb-4 text-center">Protected Page</h1>
        <div className="flex flex-col items-center mb-4">
          {user?.picture && (
            <img
              src={user.picture}
              alt="Profile"
              className="w-24 h-24 rounded-full mb-4"
            />
          )}
          <p className="text-lg mb-2 text-center">
            Welcome, <span className="font-semibold">{user?.name}</span>!
          </p>
          <p className="text-sm text-gray-600 mb-6 text-center">
            Email: <span className="font-medium">{user?.email}</span>
          </p>
        </div>
        <div className="flex justify-center">
          <AuthButton />
        </div>
      </div>
    </div>
  );
};

export default ProtectedPage;
