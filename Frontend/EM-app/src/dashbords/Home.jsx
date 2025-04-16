import React, { useEffect, useState } from 'react';
import { AppBar, Toolbar, Typography, IconButton, InputBase, Button, Container } from '@mui/material';
import { Search as SearchIcon, AccountCircle } from '@mui/icons-material';
import { jwtDecode } from "jwt-decode"; // Correct import for jwtDecode
import './Home.css';

function Home() {
  const [username, setUsername] = useState('');

  useEffect(() => {
    // Retrieve the JWT token from localStorage
    const token = localStorage.getItem('jwtToken');
    if (token) {
      try {
        const decodedToken = jwtDecode(token); // Decode the token
        setUsername(decodedToken.sub || 'User'); // Extract 'sub' field or set a default
      } catch (error) {
        console.error('Invalid token:', error);
      }
    }
  }, []);

  return (
    <div className="root">
      <AppBar position="static" className="appBar">
        <Toolbar>
          <Typography variant="h6" className="title">
            <div>
              Booking History
              &nbsp;&nbsp;
              Feed Back
            </div>
          </Typography>
          <IconButton edge="end" color="inherit" className="username-button">
            <AccountCircle />
            &nbsp; {username}
          </IconButton>
        </Toolbar>
      </AppBar>

      <div className="centered-container">
        <Container className="searchBarContainer">
          <InputBase
            placeholder="Hinted search text"
            className="searchInputBase"
            startAdornment={<SearchIcon />}
          />
        </Container>

        <Container className="mainContentContainer">
          <Typography variant="h4" align="center">
            THE LAND OF EVENTS
          </Typography>
          <Typography variant="subtitle1" align="center">
            SMALL DESCRIPTION ABOUT SITE
          </Typography>
          <Button variant="contained" color="primary" className="exploreButton">
            Explore
          </Button>
        </Container>
      </div>
    </div>
  );
}

export default Home;