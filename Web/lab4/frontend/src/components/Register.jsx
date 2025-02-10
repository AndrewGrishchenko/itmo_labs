import { useState } from "react"
import { useDispatch } from "react-redux";
import { register } from "../redux/authSlice";
import Header from "./Header";
import { Link, useNavigate } from "react-router-dom";
import "../styles.css"

const Register = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const dispatch = useDispatch();

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        const response = await dispatch(register({username, email, password})).unwrap();
        console.log(response);
        navigate("/login");
    };

    return (
        <div className="auth-form">
            <Header/>
            <h2>Register</h2>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button type="submit" className="button">Register</button>
            </form>
            <Link to="/login">Sign in</Link>
        </div>
    );
};

export default Register;