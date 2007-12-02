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

#ifndef ASSERT
#include <assert.h>
#define ASSERT assert
#endif /* ASSERT */

template<class T>
Image<T>::Image()
: m_width(0), m_height(0), m_size(0), m_data(NULL)
{}

template<class T>
Image<T>::Image(uint width, uint height)
{
	init(width, height);
}

template<class T>
Image<T>::Image(uint width, uint height, T* data)
{
	ASSERT(data == NULL);
	init(width, height);
	memcpy(m_data, data, sizeof(T)*m_size);
}

template<class T>
Image<T>::Image(uint width, uint height, const T& t)
{
	init(width, height);
	clear(t);
}

template<class T>
Image<T>::Image(const Image<T>& I)
: m_width(I.m_width), m_height(I.m_height), m_strideWidth(I.m_strideWidth), m_size(I.m_size)
{
	m_data = new T[m_size];
	memcpy(m_data, I.m_data, sizeof(T)*m_size);
}

template<class T>
Image<T>::~Image()
{
	delete[] m_data;
	m_data = NULL;
}

template<class T>
void Image<T>::init(uint width, uint height)
{
	m_width = width;
	m_height = height;
	m_strideWidth = ((m_width+3)/4)*4;
	m_size = m_strideWidth*m_height;
	m_data = new T[m_size];
}

template<class T>
void Image<T>::create(uint width, uint height)
{
	delete[] m_data;
	init(width, height);
}

template<class T>
void Image<T>::create(uint width, uint height, T* data)
{
	ASSERT(data != NULL);
	delete[] m_data;
	init(width, height);
	memcpy(m_data, data, sizeof(T)*m_size);
}

template<class T>
void Image<T>::create(uint width, uint height, const T& t)
{
	delete[] m_data;
	init(width, height);
	clear(t);
}

template<class T>
bool Image<T>::isCreated()
{
	return m_data != NULL;
}

template<class T>
void Image<T>::clear(const T& t)
{
	ASSERT(m_data != NULL);

	for (uint i = 0; i < m_size; i++)
		m_data[i] = t;
}

template<class T>
void Image<T>::set(T* data)
{
	ASSERT(m_data != NULL);

	memcpy(m_data, data, sizeof(T)*m_size);
}

template<class T>
uint Image<T>::getWidth() const
{
	return m_width;
}

template<class T>
uint Image<T>::getHeight() const
{
	return m_height;
}

template<class T>
uint Image<T>::getStride() const
{
	ASSERT((m_strideWidth % 4) == 0);
	return sizeof(T)*m_strideWidth;
}

template<class T>
const T& Image<T>::operator()(uint x, uint y) const
{
	ASSERT(x < m_width && y < m_height);
	return m_data[y*m_strideWidth+x];
}

template<class T>
T& Image<T>::operator()(uint x, uint y)
{
	ASSERT(x < m_width && y < m_height);
	return m_data[y*m_strideWidth+x];
}

template<class T>
const T& Image<T>::getPixel(uint x, uint y) const
{
	ASSERT(x < m_width && y < m_height);
	return m_data[y*m_strideWidth+x];
}

template<class T>
void Image<T>::setPixel(uint x, uint y, const T& col)
{
	ASSERT(x < m_width && y < m_height);
	m_data[y*m_strideWidth+x] = col;
}

template<class T>
Image<T>& Image<T>::operator=(const Image<T>& I)
{
	if (m_data != NULL) {
		delete[] m_data;
		m_data = NULL;
	}
	m_width = I.m_width;
	m_height = I.m_height;
	m_strideWidth = I.m_strideWidth;
	m_size = I.m_size;
	m_data = new T[m_size];
	memcpy(m_data, I.ptr(), sizeof(T)*m_size);

	return *this;
}