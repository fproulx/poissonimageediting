//////////////////////////////////////////////////////////////////////////
// Example usage for imagelib:
// Load an image, draw on it using GraphicsEx and save the result.
//////////////////////////////////////////////////////////////////////////

#include <windows.h>

#include <iostream>
using std::cout;
using std::endl;

#include "../imagelib.h"
#include "../GraphicsEx.h"
using namespace imagelib;

int main(int argc, char* argv[])
{
	try {
		// Initialize imagelib underlying GDI+ (the dtor shuts down GDI+)
		GdiplusInitializer gdiPlusInitializer;

		ImageBGRb I;
		try {
			load("input.jpg", I);
		} catch (imagelib::LoadImageFailedException& ex) {
			cout << ex.what();
		}

		GraphicsEx g(I);

		Pen p(Color::White);

		g.DrawLine(&p, 0,0,I.getWidth()-1, I.getHeight()-1);

		save("output.jpg", I);
	} catch (GdiplusInitializationException& ex) {
		cout << ex.what() << endl;
	} catch (...) {
		cout << "Unknown exception caused termination" << endl;
	}
}