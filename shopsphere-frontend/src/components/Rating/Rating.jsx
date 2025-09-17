import React, { useMemo } from 'react';
import { StarIcon } from "../common/StarIcon";
import { EmptyStarIcon } from "../common/EmptyStarIcon";

const Rating = ({ rating }) => {

    const ratingNumber = useMemo(() => {
        return Array(Math.floor(Number(rating))).fill();
    }, [rating]);

  return (
    <div className='flex items-center'>
        {ratingNumber?.map((_, index) => (
            <StarIcon key={index}/>
        ))}
        {
            new Array(5-ratingNumber?.length).fill().map((_,index)=>(
            <EmptyStarIcon key={'empty-'+index}/>
        ))
        }
        <p className='px-2 text-gray-500'>{rating}</p>
    </div>
  )
}

export default Rating