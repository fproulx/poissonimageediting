// =============================================================================
// imagelib - Image library with GDI+ wrapper (Version 1.0)
//
// A simple, lightweight image library with a GDI+ wrapper.
// Supports BMP,JPG,GIF,TIFF,PNG (through GDI+)
// =============================================================================
//
// COPYRIGHT NOTICE, DISCLAIMER, and LICENSE:
//
// imagelib : Copyright (C) 2005, Tommer Leyvand (tommerl@gmail.com)
//
// Covered code is provided under this license on an "as is" basis, without
// warranty of any kind, either expressed or implied, including, without
// limitation, warranties that the covered code is free of defects,
// merchantable, fit for a particular purpose or non-infringing. The entire risk
// as to the quality and performance of the covered code is with you. Should any
// covered code prove defective in any respect, you (not the initial developer
// or any other contributor) assume the cost of any necessary servicing, repair
// or correction. This disclaimer of warranty constitutes an essential part of
// this license. No use of any covered code is authorized hereunder except under
// this disclaimer.
//
// Permission is hereby granted to use, copy, modify, and distribute this
// source code, or portions hereof, for any purpose, including commercial
// applications, freely and without fee, subject to the following restrictions: 
//
// 1. The origin of this software must not be misrepresented; you must not
//    claim that you wrote the original software. If you use this software
//    in a product, an acknowledgment in the product documentation would be
//    appreciated but is not required.
//
// 2. Altered source versions must be plainly marked as such, and must not be
//    misrepresented as being the original software.
//
// 3. This notice may not be removed or altered from any source distribution.
//

template<class T>
tBGR<T>& tBGR<T>::operator+=(const tBGR<T>& O)
{
	b += O.b;
	g += O.g;
	r += O.r;
	return *this;
}

template<class T>
tBGR<T> tBGR<T>::operator+(const tBGR<T>& O) const
{
	tBGR<T> t(*this);
	t += O;
	return t;
}

template<class T>
tBGR<T>& tBGR<T>::operator-=(const tBGR<T>& O)
{
	b -= O.b;
	g -= O.g;
	r -= O.r;
	return *this;
}

template<class T>
tBGR<T> tBGR<T>::operator-(const tBGR<T>& O) const
{
	tBGR<T> t(*this);
	t -= O;
	return t;
}

template<class T>
tBGR<T>& tBGR<T>::operator*=(const T& t)
{
	b *= t;
	g *= t;
	r *= t;
	return *this;
}

template<class T>
tBGR<T> tBGR<T>::operator*(const T& t) const
{
	tBGR<T> r(*this);
	r *= t;
	return r;
}

template<class T>
tBGR<T>& tBGR<T>::operator/=(const T& t)
{
	b /= t;
	g /= t;
	r /= t;
	return *this;
}

template<class T>
tBGR<T> tBGR<T>::operator/(const T& t) const
{
	tBGR<T> r(*this);
	r /= O;
	return r;
}

template<class T>
bool tBGR<T>::operator==(const tBGR<T>& O) const
{
	return (b == O.b) && (g == O.g) && (r == O.r);
}

template<class T>
void gradient(const Image<T>& I, Image<tUV<T> >& O)
{
	uint w = I.getWidth();
	uint h = I.getHeight();
	O.create(w, h);

	for (uint y = 0; y < h-1; y++) {
		for (uint x = 0; x < w-1; x++) {
			tUV<T> &p = O(x,y);
			p.u = I(x+1,y) - I(x,y);
			p.v = I(x,y+1) - I(x,y);
		}
	}
}

template<class T>
Image<tUV<T> > gradient(const Image<T>& I)
{
	Image<tUV<T> > O;
	gradient(I,O);
	return O;
}

template<class T>
void divergents(const Image<tUV<T> >& I, Image<T>& O)
{
	uint w = I.getWidth();
	uint h = I.getHeight();
	O.create(w, h);

	for (uint y = 1; y < h; y++) {
		for (uint x = 1; x < w; x++) {
			T p = I(x,y).u - I(x-1,y).u +
				  I(x,y).v - I(x,y-1).v;
			O.setPixel(x,y,p);
		}
	}
}
template<class T>
Image<T> divergents(const Image<tUV<T> >& I)
{
	Image<T> O;
	divergents(I,O);
	return O;
}

template<class T>
void laplacian(const Image<T>& I, Image<T>& O)
{
	uint w = I.getWidth();
	uint h = I.getHeight();
	O.create(w, h);

	for (uint y = 1; y < h-1; y++) {
		for (uint x = 1; x < w-1; x++) {
			T f = I.getPixel(x-1,y) + I.getPixel(x+1,y) +
				I.getPixel(x,y-1) + I.getPixel(x,y+1) - I.getPixel(x,y)*4;
			O.setPixel(x,y,f);
		}
	}
}

template<class T>
Image<T> laplacian(const Image<T>& I)
{
	Image<T> O;
	laplacian(I,O);
	return O;
}

inline byte toGrayscale(tBGRb c)
{
	return (byte)((int)c.b + (int)c.g + (int)c.r) / 3;
}