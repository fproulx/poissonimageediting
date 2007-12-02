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

#pragma once

#include <stddef.h>

#pragma pack(push, 1)

namespace imagelib {

typedef unsigned int uint;

/**
 *  Image class, templated on actual pixel type.
 */
template <class T>
class Image {
public:
	/**
	 * Ctor - default, does not initial the image.
	 * @see create to initialize the image.
	 */
	Image();

	/** Copy ctor */
	Image(const Image<T>& I);

	/**
	*	Ctor .
	*	Initial pixel value will be the default ctor of type T.
	*	@param width	The width of the image.
	*	@param height	The height of the image.
	*/
	Image(uint width, uint height);

	/**
	 *	Ctor .
	 *	@param width	The width of the image.
	 *	@param height	The height of the image.
	 *	@param data		The initial data.
	 */
	Image(uint width, uint height, T* data);

	/**
	*	Ctor .
	*	@param width	The width of the image.
	*	@param height	The height of the image.
	*	@param t		The initial value for all pixels.
	*/
	Image(uint width, uint height, const T& t);

	/**
	 *	Dtor.
	 *	@note non-virtual
	 */
	~Image();

	/************************************************************************/

	/**
	*	Creates the image (clearing an already existing image).
	*	Initial pixel value will be the default ctor of type T.
	*	@param width	The width of the image.
	*	@param height	The height of the image.
	*/
	void create(uint width, uint height);

	/**
	 *	Creates the image (clearing an already existing image).
	 *	@param width	The width of the image.
	 *	@param height	The height of the image.
	 *	@param data		The initial data.
	 */
	void create(uint width, uint height, T* data);

	/**
	 *	Creates the image (clearing an already existing image).
	 *	@param width	The width of the image.
	 *	@param height	The height of the image.
	 *	@param t		The initial value for all pixels.
	 */
	void create(uint width, uint height, const T& t);

	/**
	 *  @returns 'true' if the image was created (not empty), 'false' otherwise.
	 */
	bool isCreated();

	/**
	 *	Clear the entire image to the specified value.
	 *	@param t the value for all pixels.
	 */
	void clear(const T& t);

	/**
	 *	Sets the data in this image.
	 *	@param data the data to set.
	 */
	void set(T* data);

	/**
	 *	@return the width of this image.
	 */
	uint getWidth() const;

	/**
	 *	@return the height of this image.
	 */
	uint getHeight() const;

	/**
	 *  @return the stride (offset between the begining of one scan line and the next).
	 *	@note Always a multiple of 4.
	 */
	uint getStride() const;

	/**
	 *	@return const pixel at (x,y).
	 */
	const T& operator()(uint x, uint y) const;
	/**
	 *	@return pixel at (x,y).
	 */
	T& operator()(uint x, uint y);
	/**
	 *	@return the pixel at (x,y).
	 */
	const T& getPixel(uint x, uint y) const;
	/**
	 *	Sets the pixel at (x,y).
	 */
	void setPixel(uint x, uint y, const T& col);

	T* ptr() { return m_data; }

	const T* ptr() const { return m_data; }

	uint getPtrSize() const { return m_size; }

	template<class Q>
	bool sameDimensions(const Image<Q>& I) {
		return (getWidth() == I.getWidth() && getHeight() == I.getHeight());
	}

	bool within(int x, int y) { return (x >= 0 && x < (int)m_width && y >= 0 && y < (int)m_height); }

	/**
	 *	Copy via assignment
	 */
	Image<T>& operator=(const Image<T>& I);
private:
	void init(uint width, uint height);

private:
	T* m_data;
	uint m_height, m_width, m_strideWidth, m_size;
};

#include "Image.inl"

} // namespace imagelib
#pragma pack(pop)