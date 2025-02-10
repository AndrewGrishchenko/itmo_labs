import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { logout } from "../redux/authSlice";
import Header from "./Header";
import { ListBox } from "primereact/listbox";
import { useEffect, useState } from "react";
import { Dropdown } from "primereact/dropdown";
import { Slider } from "primereact/slider";
import { Button } from "primereact/button";
import 'primereact/resources/themes/lara-light-indigo/theme.css';
import 'primereact/resources/primereact.min.css';
import '../styles.css'
import axios from "axios";

const ResultTable = ({ points }) => {
    return (
    <table border={1} style={{width: '100%', borderCollapse: 'collapse'}} className="records-table">
        <thead className="table-header">
            <tr>
                <th>X</th>
                <th>Y</th>
                <th>R</th>
                <th>Hit</th>
                <th>Current time</th>
                <th>Execution time</th>
            </tr>
        </thead>
        <tbody>
            {points.map((point, index) => (
                <tr key={index} className="table-row">
                <td>{point.cx}</td>
                <td>{point.cy}</td>
                <td>{point.r}</td>
                <td style={{color: point.hit ? 'green' : 'red'}}>{point.hit ? 'Hit' : 'not hit'}</td>
                <td>{point.curTime}</td>
                <td>{point.execTime} мкс</td>
                </tr>
            ))}
        </tbody>
    </table>
    );
};

const Home = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    // const { token } = useSelector((state) => state.auth)
    const { accessToken, refreshToken, status, error } = useSelector((state) => state.auth);

    const [selectedX, setX] = useState(null);
    const [selectedY, setY] = useState(0);
    const [selectedR, setR] = useState(0);

    const [points, setPoints] = useState([]);
    const [hidePoint, setHidePoint] = useState(false);

    const xOptions = [-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2];
    const rOptions = [0, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4];

    const API_URL = "http://localhost:8080/api"

    axios.interceptors.request.use(
        (config) => {
            if (accessToken) {
                config.headers['Authorization'] = `Bearer ${accessToken}`;
            }
            return config;
        },
        (error) => Promise.reject(error)
    );

    const handleLogout = () => {
        dispatch(logout());
        navigate("/login");
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const payload = {
            'x': selectedX,
            'y': selectedY,
            'r': selectedR
        };

        const response = await axios.post(API_URL + "/checkPoint", payload);

        let newPoint = {
            cx: response.data.x,
            cy: -response.data.y,
            r: response.data.r,
            hit: response.data.hit,
            curTime: response.data.curTime,
            execTime: response.data.execTime
        };

        setPoints([...points, newPoint]);
        setHidePoint(true);
    };

    const handleReset = (e) => {
        localStorage.removeItem('r');
        setR(null);
        setX(null);
        setY(0);
    };

    useEffect(() => {
        setR(localStorage.getItem('r') || 0);

        axios.get(API_URL + "/getPoints")
            .then(response => {
                let newPoints = [];
                response.data.forEach((point) => {
                    let newPoint = {
                        cx: point.x,
                        cy: point.y,
                        r: point.r,
                        hit: point.hit,
                        curTime: point.curTime,
                        execTime: point.execTime
                    };
                    newPoints.push(newPoint);
                });
                setPoints([...points, ...newPoints]);
            })
            .catch(error => {
                console.log("Ошибка запроса: ", error);
            });
    }, []);

    useEffect(() => {
        setHidePoint(false);
    }, [selectedX, selectedY]);

    useEffect(() => {
        localStorage.setItem("r", selectedR);
    }, [selectedR]);

    const handleClear = () => {
        axios.post(API_URL + "/removePoints");
        setPoints([]);
    };

    const handleClick = async (e) => {
        const mouseX = e.clientX;
        const mouseY = e.clientY;

        const svg = e.target.closest('svg');
        const svgRect = svg.getBoundingClientRect();

        const svgX = mouseX - svgRect.left;
        const svgY = mouseY - svgRect.top;

        const viewBoxWidth = 14;
        const viewBoxHeight = 14;
        const svgWidth = svgRect.width;
        const svgHeight = svgRect.height;

        const xInViewBox = (svgX / svgWidth) * viewBoxWidth - (viewBoxWidth / 2);
        const yInViewBox = -((svgY / svgHeight) * viewBoxHeight - (viewBoxHeight / 2));

        if (selectedR == null) {
            alert("Select R!");
            return;
        }

        const payload = {
            'x': xInViewBox,
            'y': yInViewBox,
            'r': selectedR
        };

        const response = await axios.post(API_URL + "/checkPoint", payload);

        let newPoint = {
            cx: response.data.x,
            cy: -response.data.y,
            r: response.data.r,
            hit: response.data.hit,
            curTime: response.data.curTime,
            execTime: response.data.execTime
        };

        setPoints([...points, newPoint]);
    };

    return (
        <div style={{margin: 'auto', justifyContent: 'center', alignItems: 'center'}}>
            <Header />
            <div style={{textAlign: 'center'}}>
                <button onClick={handleLogout} className="button">Log out</button>
            </div>

            <div className="tableContainer">
            <table className="pointTable">
                <tbody>
                    <tr>
                        <td scope="col" className="graphContainer">
                            <svg id="svg" width="600" height="600" viewBox="-7 -7 14 14" onClick={handleClick}>
                                <polygon fill="#F2476A" points={`0,0 0,${-selectedR} ${selectedR},${-selectedR} ${selectedR},0`}/>
                                <polygon fill="#F2476A" points={`0,0 ${-selectedR/2},0 0,${-selectedR}`}/>
                                <path fill="#F2476A" d={`M 0 0 L ${selectedR} 0 A ${selectedR} ${selectedR} 0 0 1 0 ${selectedR} Z`}/>
                                

                                <line stroke="black" strokeWidth="0.05" x1="-7" y1="0" x2="7" y2="0"/>
                                <line stroke="black" strokeWidth="0.05" x1="0" y1="-7" x2="0" y2="7"/>
                                
                                <line stroke="black" strokeWidth="0.05" x1="-6" y1="-0.2" x2="-6" y2="0.2"/>
                                <line stroke="black" strokeWidth="0.05" x1="-5" y1="-0.2" x2="-5" y2="0.2"/>
                                <line stroke="black" strokeWidth="0.05" x1="-4" y1="-0.2" x2="-4" y2="0.2"/>
                                <line stroke="black" strokeWidth="0.05" x1="-3" y1="-0.2" x2="-3" y2="0.2"/>
                                <line stroke="black" strokeWidth="0.05" x1="-2" y1="-0.2" x2="-2" y2="0.2"/>
                                <line stroke="black" strokeWidth="0.05" x1="-1" y1="-0.2" x2="-1" y2="0.2"/>
                                <line stroke="black" strokeWidth="0.05" x1="1" y1="-0.2" x2="1" y2="0.2"/>
                                <line stroke="black" strokeWidth="0.05" x1="2" y1="-0.2" x2="2" y2="0.2"/>
                                <line stroke="black" strokeWidth="0.05" x1="3" y1="-0.2" x2="3" y2="0.2"/>
                                <line stroke="black" strokeWidth="0.05" x1="4" y1="-0.2" x2="4" y2="0.2"/>
                                <line stroke="black" strokeWidth="0.05" x1="5" y1="-0.2" x2="5" y2="0.2"/>
                                <line stroke="black" strokeWidth="0.05" x1="6" y1="-0.2" x2="6" y2="0.2"/>

                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="-6" x2="0.2" y2="-6"/>
                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="-5" x2="0.2" y2="-5"/>
                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="-4" x2="0.2" y2="-4"/>
                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="-3" x2="0.2" y2="-3"/>
                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="-2" x2="0.2" y2="-2"/>
                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="-1" x2="0.2" y2="-1"/>
                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="1" x2="0.2" y2="1"/>
                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="2" x2="0.2" y2="2"/>
                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="3" x2="0.2" y2="3"/>
                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="4" x2="0.2" y2="4"/>
                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="5" x2="0.2" y2="5"/>
                                <line stroke="black" strokeWidth="0.05" x1="-0.2" y1="6" x2="0.2" y2="6"/>

                                <text fill="black" x="-6.25" y="0.625" style={{fontSize: '0.5px'}}>-6</text>
                                <text fill="black" x="-5.25" y="0.625" style={{fontSize: '0.5px'}}>-5</text>
                                <text fill="black" x="-4.25" y="0.625" style={{fontSize: '0.5px'}}>-4</text>
                                <text fill="black" x="-3.25" y="0.625" style={{fontSize: '0.5px'}}>-3</text>
                                <text fill="black" x="-2.25" y="0.625" style={{fontSize: '0.5px'}}>-2</text>
                                <text fill="black" x="-1.25" y="0.625" style={{fontSize: '0.5px'}}>-1</text>
                                <text fill="black" x="0.85" y="0.675" style={{fontSize: '0.5px'}}>1</text>
                                <text fill="black" x="1.85" y="0.675" style={{fontSize: '0.5px'}}>2</text>
                                <text fill="black" x="2.85" y="0.675" style={{fontSize: '0.5px'}}>3</text>
                                <text fill="black" x="3.85" y="0.675" style={{fontSize: '0.5px'}}>4</text>
                                <text fill="black" x="4.85" y="0.675" style={{fontSize: '0.5px'}}>5</text>
                                <text fill="black" x="5.85" y="0.675" style={{fontSize: '0.5px'}}>6</text>

                                <text fill="black" x="0.3" y="6.125" style={{fontSize: '0.5px'}}>-6</text>
                                <text fill="black" x="0.3" y="5.125" style={{fontSize: '0.5px'}}>-5</text>
                                <text fill="black" x="0.3" y="4.125" style={{fontSize: '0.5px'}}>-4</text>
                                <text fill="black" x="0.3" y="3.125" style={{fontSize: '0.5px'}}>-3</text>
                                <text fill="black" x="0.3" y="2.125" style={{fontSize: '0.5px'}}>-2</text>
                                <text fill="black" x="0.3" y="1.125" style={{fontSize: '0.5px'}}>-1</text>
                                <text fill="black" x="0.3" y="-0.875" style={{fontSize: '0.5px'}}>1</text>
                                <text fill="black" x="0.3" y="-1.875" style={{fontSize: '0.5px'}}>2</text>
                                <text fill="black" x="0.3" y="-2.875" style={{fontSize: '0.5px'}}>3</text>
                                <text fill="black" x="0.3" y="-3.875" style={{fontSize: '0.5px'}}>4</text>
                                <text fill="black" x="0.3" y="-4.875" style={{fontSize: '0.5px'}}>5</text>
                                <text fill="black" x="0.3" y="-5.875" style={{fontSize: '0.5px'}}>6</text>

                                {points.map((point, index) => (
                                    <circle 
                                        key={index}
                                        cx={point.cx}
                                        cy={point.cy}
                                        r={0.1}
                                        fill={point.hit ? 'green' : 'red'}
                                    />
                                    ))}

                                <circle fill="yellow" cx={selectedX} cy={-selectedY} r={0.1} opacity={hidePoint ? 0 : 1}/>
                            </svg>
                        </td>
                        <td scope="col">
                            <form onSubmit={handleSubmit}>
                                <table>
                                    <tbody>
                                        <tr>
                                            <td scope="col">
                                                <div className="x-select">
                                                    <label className="block mb-2">X:</label>
                                                    <ListBox 
                                                        value={selectedX}
                                                        options={xOptions.map(val => ({ label: val.toString(), value: val }))}
                                                        onChange={(e) => setX(e.value)}
                                                        className="w-full"
                                                    />
                                                </div>
                                            </td>
                                            <td scope="col">
                                                <div className="r-radio">
                                                    <label className="block mb-2">R:</label>
                                                    <ListBox
                                                        value={selectedR}
                                                        options={rOptions.map(val => ({ label: val.toString(), value: val }))}
                                                        onChange={(e) => setR(e.value)}
                                                        className="w-full grid grid-cols-2"
                                                    />
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colSpan={2}>
                                                <div className="y-select">
                                                    <label className="block mb-2">Y: {selectedY}</label>
                                                    <div style={{ backgroundColor: "green", width: "100%", height: "100%" }}>                                    
                                                        <Slider
                                                            value={selectedY}
                                                            onChange={(e) => setY(e.value)}
                                                            min={-5}
                                                            max={5}
                                                            step={0.1}
                                                        />
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>

                                <button type="submit" className="button" disabled={selectedX === null || selectedR === null}>Send</button>
                                <button type="button" className="button" onClick={handleReset}>Clear form</button>
                            </form>
                        </td>
                    </tr>
                </tbody>
            </table></div>
            <div style={{textAlign: 'center'}}>
                <button onClick={handleClear} className="button">Clear points</button>
            </div>

            <ResultTable points={points} />
        </div>
    );
};

export default Home;