import { StyledTextInput, StyledFormArea, StyledFormButton, StyledLabel, StyledSubTitle, colors, ButtonGroup, ExtraText, TextLink, CopyrightText } from './../components/Style';
import Logo from './../assets/logo.png';
import { Avatar } from '../components/Style';
import { Formik, Form } from 'formik';
import { TextInput } from '../components/FormLib';
import * as Yup from 'yup';
import { FiMail, FiLock } from 'react-icons/fi';
import { ThreeDots } from 'react-loader-spinner';
import { connect } from 'react-redux';
import { loginUser  } from '../auth/actions/userActions';
import { useNavigate } from 'react-router-dom';

const Login = ({ loginUser  }) => {
    const navigate = useNavigate();
    return (
       <div>
            <StyledFormArea>
                <Avatar image={Logo} />
                <StyledSubTitle color={colors.theme} size={30}>Member Login</StyledSubTitle>
                <Formik
                    initialValues={{ email: '', password: '' }} 
                    validationSchema={Yup.object({
                        email: Yup.string().email("Invalid email address").required("Required"),
                        password: Yup.string().min(8, "Password is too short").max(30, "Password is too long").required("Required"),
                    })}
                    onSubmit={(values, { setSubmitting, setFieldError }) => {
                        loginUser (values, navigate, setFieldError, setSubmitting);
                    }}
                >
                    {({ isSubmitting }) => (
                        <Form>
                            <TextInput 
                                name="email"
                                type="text"
                                label="Email Address"
                                placeholder="jaja@g.nsu.ru"
                                icon={<FiMail />}
                            />
                            <TextInput 
                                name="password"
                                type="password"
                                label="Password"
                                placeholder="**********"
                                icon={<FiLock />}
                            />
                            <ButtonGroup>
                                {!isSubmitting && (<StyledFormButton type="submit">Login</StyledFormButton>)}
                                {isSubmitting && (
                                    <ThreeDots 
                                        color={colors.theme}
                                        height={49}
                                        width={100}
                                    />
                                )}
                            </ButtonGroup>
                        </Form>
                    )}
                </Formik>
                <ExtraText>
                    New here? <TextLink to="/signup">Sign up</TextLink>
                </ExtraText>
            </StyledFormArea>
       </div>
    );
}

export default connect(null, { loginUser  })(Login);
