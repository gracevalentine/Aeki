exports.loginUser = (req, res) => {
    if (req.session.user) {
      return res.status(400).json({ message: "User already logged in" });
    }
  
    const { username, password } = req.body;
  
    if (!username || !password) {
      return res
        .status(400)
        .json({ message: "Username and password are required" });
    }
  
    const query = "SELECT * FROM user WHERE username = ? OR email = ?";
    db.query(query, [username, username], async (err, results) => {
      if (err) {
        console.error("Database error:", err);
        return res.status(500).json({ message: "Database error" });
      }
  
      if (results.length === 0) {
        return res.status(404).json({ message: "User not found" });
      }
  
      const user = results[0];
  
      const isMatch = await bcrypt.compare(password, user.password);
      if (!isMatch) {
        return res.status(401).json({ message: "Invalid credentials" });
      }
  
      req.session.user = {
        id_user: user.id_user,
        name: user.name,
        username: user.username,
        account_number: user.account_number,
      };
  
      const userAgent = req.headers["user-agent"] || "Unknown Device";
      const logQuery =
        "INSERT INTO log_login (id_user, name, user_agent, note) VALUES (?, ?, ?, ?)";
      db.query(
        logQuery,
        [user.id_user, user.name, userAgent, "User logged in"],
        (logErr) => {
          if (logErr) console.error("Failed to insert login log:", logErr);
        }
      );
  
      res.json({ message: "Login successful", user: req.session.user });
    });
  };