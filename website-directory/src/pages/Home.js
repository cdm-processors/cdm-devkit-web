import {
    StyledTitle,
    StyledSubTitle,
    Avatar,
    StyledButton,
    ButtonGroup,
    colors
} from "../components/Style";

// logo
import Logo from "./../assets/logo.png";

const Home = () => {
    return (
        <div
            style={{
                display: 'flex',
                height: '100vh',
                width: '100%',
            }}
        >
            {/* Левая сторона: Контент */}
            <div
                style={{
                    flex: 1,
                    backgroundColor: '#fff',
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    padding: '50px',
                    position: 'relative',
                }}
            >
                {/* Логотип в левом верхнем углу */}
                <div
                    style={{
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        padding: '15px',
                        display: 'flex',
                        justifyContent: 'flex-start',
                    }}
                >
                    <Avatar image={Logo} />
                </div>

                {/* Центральный контент */}
                <div style={{ textAlign: 'center', maxWidth: '400px' }}>
                    <StyledTitle size={40} color={colors.theme}>
                        CDM Devkit Web
                    </StyledTitle>
                    <StyledSubTitle size={20} color={colors.dark2} style={{ marginBottom: '30px' }}>
                        Welcome to the web application of CDM Devkit
                    </StyledSubTitle>
                    <ButtonGroup>
                        <StyledButton
                            to="/login"
                            style={{
                                backgroundColor: '#000',
                                color: '#fff',
                                borderRadius: '25px',
                                padding: '10px 30px',
                                marginRight: '10px',
                            }}
                        >
                            Login
                        </StyledButton>
                        <StyledButton
                            to="/signup"
                            style={{
                                backgroundColor: '#000',
                                color: '#fff',
                                borderRadius: '25px',
                                padding: '10px 30px',
                            }}
                        >
                            Signup
                        </StyledButton>
                    </ButtonGroup>
                </div>
            </div>

            {/* Правая сторона: Картинка */}
            <div
                style={{
                    flex: 1,
                    backgroundImage: `url(${require('./../assets/newback2.jpg')})`,
                    backgroundSize: 'cover',
                    backgroundRepeat: 'no-repeat',
                    backgroundPosition: 'center',
                }}
            />
        </div>
    );
};

export default Home;
