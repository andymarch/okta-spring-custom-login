# Okta Spring Custom Login Experiance

This project demonstrates a minimal implementation to allow for custom login experiances 
using Spring OAuth.

AuthN is completed using the Okta Auth SDK to retrieve a session token. This session token
is then injected into the subsequent authorize call allowing the user to bypass the Okta login
experience.