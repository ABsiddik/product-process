export default function Header() {
    return(
        <header className="bg-white shadow sticky top-0 z-50">
            <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
                {/* Logo */}
                <a href="/" className="text-2xl font-bold text-blue-600">
                Product App
                </a>

                <nav className="hidden md:flex space-x-6 text-gray-700 font-medium">
                    <a href="/products/upload" className="hover:text-blue-600">Bull Upload</a>
                    <a href="/products" className="hover:text-blue-600">List</a>
                </nav>
            </div>
        </header>
    );
}