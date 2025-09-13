import React from "react";

const Navigation = () => {
  return (
    <nav className="flex items-center py-6 px-16 justify-between gap-20 custom-nav">
      <div className="flex items-center gap-6">
        {/* Logo */}
        <a className="text-3xl text-black font-bold gap-8" href="/">
          ShopSphere
        </a>
      </div>
    </nav>
  );
};

export default Navigation;
