function verifyEmail(email) {
    const emailRegex = new RegExp("^[a-zA-Z0-9._-]+@[qQ][qQ]\\.com$");
    return emailRegex.test(email);
}

export {
    verifyEmail
}