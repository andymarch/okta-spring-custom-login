# Okta Spring Custom Login Experiance

This project demonstrates a minimal implementation to allow for custom login UI 
using Spring OAuth with Okta.

AuthN is completed in the LoginController using the Okta Auth SDK to retrieve a session token. This session token is 
stored as a flash attribute which is then read by OktaSessionTokenAuthorizationRequestResolver by including the session 
token in the authorize call the Okta hosted login experience is bypassed.