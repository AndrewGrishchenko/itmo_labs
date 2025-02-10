import { useState } from "react"
import { useDispatch, useSelector } from "react-redux";
import { login } from "../redux/authSlice";
import Header from "./Header";
import { Link, useNavigate } from "react-router-dom";
import "../styles.css"

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const dispatch = useDispatch();
    const { status, error } = useSelector((state) => state.auth);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await dispatch(login({ username, password })).unwrap();
            if (response.status === "Ok") {
                console.log("Авторизация успешна: ", response);
                navigate("/home");
            } else {
                console.error("Ошибка: ", response.message);
            }
        } catch (error) {
            console.error("Ошибка авторизации", error);
        }
    };
    
    return (
        <div className="auth-form">
            <Header/>
            <h2>Login</h2>
            <form onSubmit={handleSubmit} className="loginBox">
                <input
                    type="login"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button type="submit" className="button">Login</button>
            </form>
            {status === 'loading' && <p>Loading...</p>}
            {error && <p>Error: {error}</p>}
            <Link to="/register">Sign up</Link>
        </div>
    );
};

export default Login;